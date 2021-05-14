package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoTest {
  private Cuenta cuenta;
  private Movimiento movimiento;

  @BeforeEach
  void init() {
    cuenta = new Cuenta();
    movimiento = new Movimiento(LocalDate.now(), 5000, true);
  }

  @Test
  void PonerUnDepositoPositivo() {
    cuenta.poner(1500);
    assertEquals(cuenta.getSaldo(), 1500);
  }

  @Test
  void PonerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.poner(-1500));
  }

  @Test
  void PonerTresDepositos() {
    cuenta.poner(1500);
    cuenta.poner(456);
    cuenta.poner(1900);
    assertEquals(cuenta.getSaldo(), 3856);
  }

  @Test
  void PonerMasDeTresDepositos() {
    assertThrows(MaximaCantidadDepositosException.class, () -> {
          cuenta.poner(1500);
          cuenta.poner(456);
          cuenta.poner(1900);
          cuenta.poner(245);
    });
  }

  @Test
  void ExtraerMasQueElSaldo() {
    assertThrows(SaldoMenorException.class, () -> {
          cuenta.setSaldo(90);
          cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMasDe1000() {
    assertThrows(MaximoExtraccionDiarioException.class, () -> {
      cuenta.setSaldo(5000);
      cuenta.sacar(1001);
    });
  }

  @Test
  public void ExtraerMontoNegativo() {
    assertThrows(MontoNegativoException.class, () -> cuenta.sacar(-500));
  }

  @Test
  public void ExtraerSaldo(){
    cuenta.poner(1500);
    cuenta.sacar(500);
    assertEquals(cuenta.getSaldo(),  1000);
  }

  @Test
  public void MontoExtraidoEnUnaFecha(){
    cuenta.poner(1000);
    cuenta.sacar(500);
    cuenta.sacar(250);
    assertEquals(cuenta.getMontoExtraidoA(LocalDate.now()), 750);
  }

  @Test
  public void FechaDeUnMovimiento(){ //no estoy segura si tiene sentido testear esto
    assertTrue(movimiento.esDeLaFecha(LocalDate.now()));
  }

}