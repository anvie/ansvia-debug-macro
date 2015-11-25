package com.ansvia.macros


import language.experimental.macros
import scala.reflect.macros.whitebox.Context

trait DebugMacros {
    def info(params: Any*):Unit = macro DebugMacros.info_impl
    def debug(params: Any*):Unit = macro DebugMacros.debug_impl
    def warn(params: Any*):Unit = macro DebugMacros.warn_impl
    def error(params: Any*):Unit = macro DebugMacros.error_impl
}

object DebugMacros extends DebugMacros {

    private val isDisabled = {
        System.getenv("ANSVIA_DEBUG") match {
            case null => false
            case x if x.trim().toLowerCase == "false" =>
                true
            case _ => false
        }
    }

    def info_impl(c:Context)(params:c.Expr[Any]*):c.Expr[Unit] = _print_impl("[INFO]: ")(c)(params: _*)
    def debug_impl(c:Context)(params:c.Expr[Any]*):c.Expr[Unit] = _print_impl("[DEBUG]: ")(c)(params: _*)
    def warn_impl(c:Context)(params:c.Expr[Any]*):c.Expr[Unit] = _print_impl("[WARN]: ")(c)(params: _*)
    def error_impl(c:Context)(params:c.Expr[Any]*):c.Expr[Unit] = _print_impl("[ERROR]: ")(c)(params: _*)

    def _print_impl(prefix:String)(c:Context)(params:c.Expr[Any]*):c.Expr[Unit] = {
        import c.universe._

        val prefixExpr = c.Expr[String](Literal(Constant(prefix)))

        if (!isDisabled || prefix == "[ERROR]: "){

            val trees = params.map { param =>
                param.tree match {
                    case Literal(Constant(_const)) =>
                        val reified = reify {
                            print(param.splice)
                        }
                        reified.tree

                    case _ =>
                        val paramRep = show(param.tree)
                        val paramRepTree = Literal(Constant(paramRep))
                        val paramRepExpr = c.Expr[String](paramRepTree)
                        val reified = reify {
                            print(paramRepExpr.splice + " = " + param.splice)
                        }
                        reified.tree

                }
            }

//            c.enclosingPosition.

            val seps = (1 to trees.size-1).map(_ => reify {
                print(", ")
            }.tree) :+ reify {
                println()
            }.tree
            val separatorTree = reify { print(" - ") }.tree

            val logLevelTree = reify { print(prefixExpr.splice) }.tree
            val clazzExprTree = Apply(Select(Ident(TermName("Predef")), TermName("print")),
                List(
                    Apply(Select(Ident(TermName("getClass")), TermName("getSimpleName")), Nil)
                )
            )

            val treeSeps =  List(logLevelTree, clazzExprTree, separatorTree) ++
                    trees.zip(seps).flatMap(p => List(p._1, p._2))

            c.Expr[Unit](Block(treeSeps.toList, Literal(Constant(()))))

        }else{
            reify { () }
        }
    }
}

