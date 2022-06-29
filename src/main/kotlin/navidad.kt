import java.time.LocalDate
class Persona{
    lateinit var criterioDelRegalo : CriterioRegalo // Declaro un atributo del tipo CriterioRegalo para realizar la asociación entre la persona y el Regalo
    // de esta forma puedo encapsular logica de comportamiento y diseñar un Strategy según la necesidad de la Persona

    // Agrego atributos faltantes que considero que tiene que tener la persona
    var email : String = " "
    var direccion : String = " "
    var nombre :  String = " "
    var dni: String = " "

    // Defino la funcion leGustaRegalo que encapsula la logica del criterioDelRegalo.
    fun leGustaRegalo(regalo: Regalo) = criterioDelRegalo.gustosDeRegalo(regalo)
}

// Es la interfaz del Strategy y entidad padre de los subcriterios
interface CriterioRegalo{
    fun gustosDeRegalo(regalo : Regalo) : Boolean
}

// Coloco constructor en las clases que implementan la interfaz CriterioRegalo
// le saco la pluralidad a mis subclases, porque cuando uno instancia un objeto, es decir crearlo a partir de una clase, se crea un solo objeto de esta clase.
// subclase del strategy
class Conformista() : CriterioRegalo{
    override fun gustosDeRegalo(regalo : Regalo) = true
}

// subclase del strategy
class Interesado(var ciertaCantidadDePlata : Double) : CriterioRegalo{ //muevo la variable cierta cantidadDePlata al constructor asi puedo modificar su valor
    override fun gustosDeRegalo(regalo : Regalo) = (regalo.costoDelRegalo > ciertaCantidadDePlata)
}

// subclase del strategy
class Exigente() : CriterioRegalo{
    override fun gustosDeRegalo(regalo : Regalo) = regalo.esRegaloValioso()
}
// subclase del strategy
class Marquera(var marcaRecibida: String) : CriterioRegalo{ //Cambio el tipo marcaRecibida de Marca a String, porque decidí no usar a Marca como clase por ser innecesaria.

    // modifico la función para que de True solo si la marca, que es un atributo del regalo que viene por parametro, apunta a la misma referencia que la del atributo marcaRecibida del constructor
    override fun gustosDeRegalo(regalo : Regalo) = regalo.marca == marcaRecibida
}

class Combineta() : CriterioRegalo{
    // acá encontramos un Composite dentro del Strategy, ya que permite componer objetos en estructuras de tipo arbol, es decir, con hojas y ramas, de esta forma
    // podemos anidar entidades dentro de otras y armar jerarquias.
    // Aplica polimorfismo y esto nos permite crear algoritmos con mayor comodidad.

    var criteriosEleccionRegalo = mutableListOf<CriterioRegalo>()

    override fun gustosDeRegalo(regalo : Regalo) = criteriosEleccionRegalo.any { criterio -> criterio.gustosDeRegalo(regalo)}

}

// Al principio la pensé como una interfaz, ya que siempre comienzo con interfaces dado a que es una cascara,
// Pero luego de ver que requiere de un estado presente que va variando , sumado a que con el Template Method diseñé una clase abstracta
// Implemente al regalo como clase abstracta.

abstract class Regalo(var costoDelRegalo: Double, var marca: String){
    // al atributo marcaDeMarcasCaras le cambié el nombre y le puse marca, y es de tipo String.
    // costoDelRegalo lo coloco Double en vez de Int.
    // Tanto el atributo marca como el de costoDelRegalo los pondré en el constructor asi pasan a ser property
    // al colocarlos en el constructor de Regalo, tambien lo tendré que poner en el constructor de las subclases.

    // hago que el atributo precioDeRegaloValioso tenga un .0 para hacerlo Double.
    val precioDeRegaloValioso = 5000.0
    var id : Int = 0
    //Acá aplicamos el Template Method, que sirve para definir el esqueleto de un algoritmo
    //De esta forma podemos tener un comportamiento en una clase padre y luego ese comportamiento ir modificandose
    //en las distintas subclases sin cambiar la estructura del algoritmo.

    fun esRegaloValioso() = condicionDePrecioDeRegaloValioso() && caracteristicaEspecificaDelRegalo()

    // Esta es la primitiva que luego se sobreescribe en las subclases que la implementan
    abstract fun caracteristicaEspecificaDelRegalo() : Boolean

    fun condicionDePrecioDeRegaloValioso() = costoDelRegalo > precioDeRegaloValioso
}

class Ropa(costoDelRegalo: Double, marca: String) : Regalo(costoDelRegalo, marca)  {
    // Elimino el atributo anterior ya que no lo utilizaré.
    // Coloco el atributo marcas como una lista inmutable para ahora si tener mis marcas especificas de manera correcta como una listado de strings.
    val marcasChetas = listOf<String>("Jordache", "Lee", "Charro", "Motor Oil")

    //Primitiva
    // modifico la funcion overrideada caracteristicaEspecificaDelRegalo para que solo de TRUE si marchasChetas contiene a marca que es el atributo que está en el constructor de la clase.
    override fun caracteristicaEspecificaDelRegalo() = marcasChetas.contains(marca)

}

class Jueguete(costoDelRegalo: Double, marca: String) : Regalo(costoDelRegalo, marca){
    var fechaQueSeLanzaron: LocalDate = LocalDate.now()

    //Primitiva
    override fun caracteristicaEspecificaDelRegalo() = fechaQueSeLanzaron.year < 2000

}

class Perfume(costoDelRegalo: Double, marca: String) : Regalo(costoDelRegalo, marca){
    var origen = ORIGEN.NACIONAL

    //Primitiva
    override fun caracteristicaEspecificaDelRegalo() = origen == ORIGEN.EXTRANJERO
}

// AGREGO LA CLASE FALTANTE EXPERIENCIA.
class Experiencia(costoDelRegalo: Double, marca: String) : Regalo(costoDelRegalo, marca) {

    var fechaDeLaExperiencia: LocalDate = LocalDate.now() // coloco un atributo localdate
    val diaViernes = 5 // por ser el quinto dia de la semana

    // overrideo la funcion criterioEspecifico para que solo de TRUE si el valor del dia de la semana del atributo fechaDeLaExperiencia es igual al del diaViernes
    override fun caracteristicaEspecificaDelRegalo() = fechaDeLaExperiencia.dayOfWeek.value == diaViernes
}

// AGREGO EL VAUCHER COMO UNA CLASE MAS QUE HEREDA DE REGALO (PUNTO 3)
// Coloco en el constrcutor de Regalo el costoDelRegalo, y la marca como un string.
// La funcion overrideada de caracteristicaEspecificaDelRegalo debe retornar False, ya que no se considera un regalo valioso.
class Voucher(costoDelRegalo: Double, marca: String) : Regalo(costoDelRegalo, marca){

    override fun caracteristicaEspecificaDelRegalo() = false

}

// Elimino la clase Marca y su herencia debido a que es innecesaria. El listado de Marcas lo colocaré como atributo en la clase Ropa.


// ENUM para simplificar
enum class ORIGEN {
    NACIONAL,
    EXTRANJERO
}


// Decidi volver a diseñar el proceso de nuevo de 0 porque no me convencia lo que tenia definido.
// Creo el proceso como un objeto.
object Proceso {
    var regalosEnStock = mutableListOf<Regalo>() // tiene que tener una lista de Regalos y establecer una relacion de asociacion entre el sistema y la clase Regalo
    var regalosRealizadosObservers = mutableListOf<RegaloEntregadoObserver>() // acá guardamos la lista de Observers, modifico el nombre para que sea mas claro
    var regalosEntregados = mutableListOf<RegaloEntregadoYRegistrado>() //acá guardo un listado de los regalosentregados, que tiene en el atributo de esta dataclass a la persona y al regalo

    // Compruebo si exsiste un regalo adecuado para una persona, cocolando un any en mi coleccion de Regalos en stock ya que devolverá un TRUE si alguno cumple dicha condición.
    // dentro del bloque, colocaré a la persona que le paso por parametro de la funcion. con la función de persona leGustaRegalo, y el regalo que será el it.
    fun existeRegaloAdecuadoParaLaPersona(persona: Persona) = regalosEnStock.any{persona.leGustaRegalo(it)}

    // creo esta funcion para que me devuelva el primer regalo que cumpla la condicion.
    // dentro del bloque, colocaré a la persona que le paso por parametro de la funcion. con la función de persona leGustaRegalo, y el regalo que será el it.
    fun regaloAdecuadoParaLaPersona(persona: Persona) = regalosEnStock.first { persona.leGustaRegalo(it) }


    // creo esta funcion para decidir si retornar el regalo adecuado para la persona o el Vaucher dependiendo si existe un regalo adecuado o no
    fun regaloAdecuadoParaLaPersonaOVaucher(persona: Persona) : Regalo{
        if(existeRegaloAdecuadoParaLaPersona(persona)){
            return regaloAdecuadoParaLaPersona(persona) // acá devuelvo el primer regalo que cumple la condicion
        }
        else{
            return Voucher(2000.0,"Papapp") // acá devuelvo el Voucher, coloco por parametros el valor del costo y la marca.
        }
    }

    // Creo una nueva funcion para realizar el regalo, lo que consiste en la entrega del mismo a la persona y la accion de los observers.
    // Esta funcion tiene el apoyo de otras funciones para poder realizar su funcion
    fun realizarRegalo(persona: Persona){
        var regaloAdecuado = regaloAdecuadoParaLaPersonaOVaucher(persona) // almaceno el objeto regalo que viene de esta funcion en esta variable de la función
        entregaDeRegalo(persona,regaloAdecuado) // llamo a la función entregaDeRegalo para entregar el regalo y modificar las listas que tiene el Proceso.

        // esta acción desencadena el comienzo del observer, el cual observará a cada una de los regalos entregados para su posterior funcion con el metodo regaloEntregado() pertenieciente a la interface RegaloEntregadoObserver
        // le paso como parametros, a la funcion regaloEntregado(), la persona proveniente del parametro de esta propia funcion, y el objeto regalo que almacene en la variable de esta funcion
        regalosRealizadosObservers.forEach { it.regaloEntregado(persona,regaloAdecuado) }
    }

    // funcion para agregarRegalos a la lista de regalos
    fun agregarRegaloEnStock(regalo: Regalo){
        regalosEnStock.add(regalo)
    }

    // funcion para agregar un nuevo observer a la lista de observers, ya que pueden haber nuevas implementaciones a futuro
    fun agregarObserver(nuevoObserver: RegaloEntregadoObserver){
        regalosRealizadosObservers.add(nuevoObserver)
    }

    // modificio el nombre de la funcion ya que acá se hace la entrega del regalo
    // esta funcion se usa en realizarRegalo pero la logica la coloco aqui para abstraer la informacion
    // se agrega el regalo y la persona a la lista de regalos entregados.
    // se remueve el regalo de la lista de regalos en stock
    fun entregaDeRegalo(persona: Persona, regalo: Regalo){
        regalosEntregados.add(RegaloEntregadoYRegistrado(persona,regalo))
        regalosEnStock.remove(regalo)
    }
}

//creo una data class del RegaloEntregadoYRegistrado que tiene atributos que tienen relacion de asociacion con persona y regalo reespectivamente.
// Arreglo error de convención y coloco la primer letra del nombre de la clase en mayuscula
data class RegaloEntregadoYRegistrado(val persona: Persona, val regalo: Regalo)

interface RegaloEntregadoObserver{ //para el observer hay una interfaz observer
    fun regaloEntregado(persona: Persona, regalo: Regalo) //necesito agregar además del regalo, a la persona como parametro porque el mail debe ser entregado a una persona del clase Persona
}

// para el observer creamos la primera entidad concreta que en este caso es MailObserver.
class MailObserver(var mailSender : MailSender) : RegaloEntregadoObserver{ //pasamos el mailSender en el constructor porque es algo que necesitamos si o si y podemos evitar problemas que surgirian al no inicializarlo

    // overrideo la funcion regaloEntregado utilizando un constructor injection para poder realizar el requerimiento de mandar un mail con los datos de la persona.
    override fun regaloEntregado(persona: Persona, regalo: Regalo) {
        mailSender.sendMail( //instancio un objeto mail
            Mail(
                to = persona.email, //acá le manda el email a la persona
                from = "jojojo@mail.com",
                subject = "Regalo de navidad enviado",
                content = "Se ha enviado un regalo para ${persona.nombre}") //le coloco el nombre de la persona a la que le mando el email
        )
    }
}


// para el observer creamos la segunda entidad concreta que en este caso es AvisarAlFlete.
// Coloco el nombre AvisarAlFlete en vez de InformeObserver
class AvisarAlFlete(val flete : Flete) : RegaloEntregadoObserver{ //coloco un atributo en el constructor de tipo Flete

    // overrideo la funcion regaloEntregado utilizando un constructor injection para poder realizar el requerimiento de informar el regalo con los datos del cliente.
    override fun regaloEntregado(persona: Persona, regalo: Regalo) {
        flete.informarElRegalo( //llamo al metodo del flete y le paso los valores por parametros, estos valores son correspondientes a los atributos que tenia en la clase persona y el atributo codigo del Regalo
            Informe(persona.direccion,
                persona.nombre,
                persona.dni,
                regalo.id)
        )
    }
}

//AGREGO LA TERCERA CLASE OBSERVER CONCRETA
class ModificarCriterioDePersonaInteresada : RegaloEntregadoObserver{
    var montoMaximoDelRegalo = 10000.0
    var nuevoMontoMaximo = 5000.0

    //overrideo la funcion regaloEntregado, en la cual modifica el criterioDelRegalo para que si se cumple esta condicion el criterio pase a ser de Interesado
    override fun regaloEntregado(persona: Persona, regalo: Regalo) {
        if (regalo.costoDelRegalo > montoMaximoDelRegalo){
            persona.criterioDelRegalo = Interesado(nuevoMontoMaximo)
        }
    }

}


// Esta es una interfaz de Salida del Mail, y gracias al MailSender podemos cumplir con el mandado de los mails e informes
// Ya que necesitabamos de las funcionalidades de otro objeto
interface MailSender{
    fun sendMail(notificacion : Mail)
}

//Agrego una interfaz de salida Flete porque solo es una cascara que se comporta como una interfaz saliente.
//Gracias a esta interfaz y su función podemos cumplir con el requerimiento de informar sobre el regalo.
interface Flete{
    // esta funcion se encarga de pasar los datos del cliente
    fun informarElRegalo(notificacion : Informe)

}


// Data class, son clases con el proposoito de almacenar valores.
data class Mail(val to: String, val from: String, val subject: String, val content: String)
data class Informe(val direccionCliente: String, val nombreCliente: String, val dniCliente: String, val codigoRegalo: Int)