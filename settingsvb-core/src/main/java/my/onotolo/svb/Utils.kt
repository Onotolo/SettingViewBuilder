package my.onotolo.svb

class ClassFuncPair<T>(
    val clazz: Class<T>,
    val bindFunction: BindFunction<T>
)

class ClassFuncMap(vararg val pairs: ClassFuncPair<*>) {

    operator fun<T> get(clazz: Class<T>): BindFunction<T>? {
        return pairs.find {
            it.clazz == clazz
        }?.bindFunction as BindFunction<T>
    }
}

infix fun <T> Class<T>.toFunc(that: BindFunction<T>) = ClassFuncPair(this, that)

fun mapOf(vararg classToFunc: ClassFuncPair<*>) = ClassFuncMap(*classToFunc)