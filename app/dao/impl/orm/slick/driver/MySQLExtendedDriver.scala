package dao.impl.orm.slick.driver

import slick.ast.{FunctionSymbol, Node, LiteralNode}
import slick.driver.{MySQLDriver, QueryBuilderInput}
import slick.util.MacroSupport.macroSupportInterpolation


trait MySQLExtendedDriver extends slick.driver.MySQLDriver {
  override def createQueryBuilder(input: QueryBuilderInput): QueryBuilder =
    new QueryBuilder(input)

  class QueryBuilder(input: QueryBuilderInput) extends super.QueryBuilder(input) {
    override def expr(n: Node, skipParens: Boolean = false) = n match {
      case MySQLExtendedDriver.As(column, LiteralNode(name: String)) => {
        b"("
        super.expr(column, skipParens)
        b") as ${name}"
      }
      case _ => super.expr(n, skipParens)

    }

  }
}
object MySQLExtendedDriver extends MySQLExtendedDriver {
  val As = new FunctionSymbol("As")

  trait SimpleQL extends super.SimpleQL {
    implicit class RichQuery[T: TypeMapper](q: Query[Column[T], T]) {

      def as(name: String) =
        MySQLExtendedDriver.As.column[T](Node(q.unpackable.value), name)
    }
  }
  override val simple: SimpleQL = new SimpleQL {}
}
