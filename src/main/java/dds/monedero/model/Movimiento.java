package dds.monedero.model;

import java.time.LocalDate;

public class  Movimiento {
  private LocalDate fecha;
  // Nota: En ningún lenguaje de programación usen jamás doubles (es decir, números con punto flotante) para modelar dinero en el mundo real.
  // En su lugar siempre usen numeros de precision arbitraria o punto fijo, como BigDecimal en Java y similares
  // De todas formas, NO es necesario modificar ésto como parte de este ejercicio. 
  private double monto;
  private boolean esDeposito; //PRIMITIVE OBSESSION, el "desposito" puede tener su propio comportamiento


  public Movimiento(LocalDate fecha, double monto, boolean esDeposito) {
    this.fecha = fecha;
    this.monto = monto;
    this.esDeposito = esDeposito;
  }

  public double getMonto() {
    return monto;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  //no tiene mucho sentido tenerlos, si cambio el booleano tienen el mismo comportamiento que esDeLaFecha (solamente interesa fijarse la fecha)
  //DUPLICATED METHOD
  public boolean fueDepositado(LocalDate fecha) {
    return isDeposito() && esDeLaFecha(fecha);
  }

  public boolean fueExtraido(LocalDate fecha) {
    return isExtraccion() && esDeLaFecha(fecha);
  }

  public boolean esDeLaFecha(LocalDate fecha) {
    return this.fecha.equals(fecha);
  }

  //TYPE TEST en idDeposito y en isEstraccion
  public boolean isDeposito() {
    return esDeposito;
  }

  public boolean isExtraccion() {
    return !esDeposito;
  }

  //MISSPLACED METHOD: de esto debería encargarse la cuenta, no el movimiento
  public double calcularValor(Cuenta cuenta) {
    if (esDeposito) {
      return cuenta.getSaldo() + getMonto();
    } else {
      return cuenta.getSaldo() - getMonto();
    }
  }
}
