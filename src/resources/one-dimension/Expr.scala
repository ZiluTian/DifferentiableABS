sealed trait Expr
final case class Const(value: Double) extends Expr
final case class Var(value: Double) extends Expr
final case class Mul(left: Expr, right: Expr) extends Expr
final case class Div(left: Expr, right: Expr) extends Expr
final case class Add(left: Expr, right: Expr) extends Expr
final case class Sub(left: Expr, right: Expr) extends Expr
final case class Pow(expr: Expr, power: Double) extends Expr
final case class Sin(expr: Expr) extends Expr
final case class Cos(expr: Expr) extends Expr
final case class Tan(expr: Expr) extends Expr
final case class Exp(expr: Expr) extends Expr

def exprToDual(expr: Expr): Dual[Double] =
    expr match {
        case Var(value) => Dual(value)
        case Const(value) => Dual.const(value)
        case Pow(expr, power) => exprToDual(expr).pow(power)
        case Tan(expr) => exprToDual(expr).tan
        case Exp(expr) => exprToDual(expr).exp
        case Cos(expr) => exprToDual(expr).cos
        case Sin(expr) => exprToDual(expr).sin
        case Add(left, right) => exprToDual(left) + exprToDual(right)
        case Mul(left, right) => exprToDual(left) * exprToDual(right)
        case Sub(left, right) => exprToDual(left) - exprToDual(right)
        case Div(left, right) => exprToDual(left) / exprToDual(right)
}

def exprToReverse(expr: Expr): Reverse[Double] =
    expr match {
        case Var(value) => Reverse(value)
        case Const(value) => Reverse.const(value)
        case Pow(expr, power) => exprToReverse(expr).pow(power)
        case Tan(expr) => exprToReverse(expr).tan
        case Exp(expr) => exprToReverse(expr).exp
        case Cos(expr) => exprToReverse(expr).cos
        case Sin(expr) => exprToReverse(expr).sin
        case Add(left, right) => exprToReverse(left) + exprToReverse(right)
        case Mul(left, right) => exprToReverse(left) * exprToReverse(right)
        case Sub(left, right) => exprToReverse(left) - exprToReverse(right)
        case Div(left, right) => exprToReverse(left) / exprToReverse(right)
}

// Example
val x: Expr = Add(Const(10), Pow(Var(5), 2))
val dualExpr = exprToDual(x)
val reverseExpr = exprToReverse(x)
// 10: a single variable x at value 5
assert(dualExpr.derivative == reverseExpr.derivative)