package sass.compose.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import sass.compose.annotation.NavigationNode

class NodeProcessor(
    private val logger: KSPLogger,
    private val codeGenerator: CodeGenerator
): SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("NodeProcessor was invoked.")
        val symbols = resolver.getSymbolsWithAnnotation(NavigationNode::class.qualifiedName!!)
        val ret = mutableListOf<KSAnnotated>()
        symbols.forEach { symbol ->
            if (symbol is KSFunctionDeclaration && symbol.validate()) {
                val annotation = symbol.annotations.first { it.shortName.asString() == "NavigationNode" }
                val route = annotation.arguments.first { it.name?.asString() == "route" }.value as String
                val args = annotation.arguments.first { it.name?.asString() == "args" }.value as ArrayList<String>
                val optionalArgs = annotation.arguments.first { it.name?.asString() == "optionalArgs" }.value as ArrayList<String>
                generateCode(symbol, route, args, optionalArgs)
            } else {
                ret.add(symbol)
            }
        }
        return ret
    }

    private fun generateCode(symbol: KSFunctionDeclaration, route: String, args: ArrayList<String>,
                             optionalArgs: ArrayList<String>) {
        val functionName = symbol.simpleName.asString()
        val packageName = symbol.packageName.asString()
        val fileName = "${functionName}GraphNode"

        val typeBuilder = TypeSpec.objectBuilder(fileName)
        typeBuilder.addProperty(
            PropertySpec.builder("rawRoute", String::class).initializer("%S", route).addModifiers(
                KModifier.PUBLIC).build()
        ).addProperty(
            PropertySpec.builder("route", String::class).initializer("%S", generateRoute(
                route, args, optionalArgs
            )).addModifiers(
                KModifier.PUBLIC).build()
        ).addProperty(
            PropertySpec.builder("args", Array::class.parameterizedBy(String::class))
                .initializer("arrayOf(${args.joinToString { "\"$it\"" }})")
                .addModifiers(KModifier.PRIVATE)
                .build()
        ).addProperty(
            PropertySpec.builder("optionalArgs", Array::class.parameterizedBy(String::class))
                .initializer("arrayOf(${optionalArgs.joinToString { "\"$it\"" }})")
                .addModifiers(KModifier.PRIVATE)
                .build()
        ).addFunction(generateNavigationRoute(args, optionalArgs))

        optionalArgs.forEach {
            typeBuilder.addProperty(
                PropertySpec.builder("ARG_OPTIONAL_$it",String::class).initializer("%S", it).addModifiers(KModifier.CONST).build()
            )
        }

        args.forEach {
            typeBuilder.addProperty(
                PropertySpec.builder("ARG_$it",String::class).initializer("%S", it).addModifiers(KModifier.CONST).build()
            )
        }


        val fileSpec = FileSpec.builder(packageName, fileName).addType(typeBuilder.build()).build()

        val dependencies = Dependencies(false, symbol.containingFile!!)
        val file = codeGenerator.createNewFile(dependencies, packageName, fileName)
        file.bufferedWriter().use { fileSpec.writeTo(it) }
    }

    private fun generateRoute(rawRoute: String, args: ArrayList<String>, optionalArgs: ArrayList<String>): String {
        val route = buildString {
            append(rawRoute)

            args.forEach {
                append("/{$it}")
            }
            if (optionalArgs.isNotEmpty()) {
                append("?")

                optionalArgs.forEachIndexed { index, it ->
                    append("$it={$it}")
                    if (index < optionalArgs.size - 1) {
                        append("&")
                    }
                }
            }
        }

        return route
    }



    private fun generateNavigationRoute(args: ArrayList<String>, optionalArgs: ArrayList<String>): FunSpec {
        val funBuilder = FunSpec.builder("navigationRoute")
        val argsCode = buildString {
            args.forEach {
                funBuilder.addParameter(
                    "arg_$it",
                    String::class
                )

                append("""
                append("/${'$'}arg_$it")
                """.trimIndent())
                append("\n")
            }
        }

        val optionalArgsCode = buildString {
            optionalArgs.forEachIndexed { index, it ->
                funBuilder.addParameter(
                    "optionalArg_$it",
                    String::class.asTypeName().copy(nullable = true)
                )

                if (index == 0)
                    append("append(\"?\")")
                append("\n")
                append("""
                    if (optionalArg_$it != null) {
                        append("$it=${'$'}optionalArg_$it")
                """.trimIndent())
                append("\n")
                if (index < optionalArgs.size - 1) {
                    append("\t")
                    append("""
                        append("&")
                    """.trimIndent())
                    append("\n")
                }
                append("}")
            }
        }


        val code = buildString {
            append("val ret = buildString {\n")
            append("append(rawRoute)\n")
            append(argsCode)
            append(optionalArgsCode)
            append("}\n")
            append("return ret")
        }

        funBuilder.returns(String::class).addCode(
            code
        )

        return funBuilder.build()
    }
}