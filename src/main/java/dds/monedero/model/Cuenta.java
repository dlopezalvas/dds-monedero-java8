package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

    private double saldo = 0;
    private List<Movimiento> movimientos = new ArrayList<>();

    public Cuenta() {
        saldo = 0;
    }

    public Cuenta(double montoInicial) {
        saldo = montoInicial;
    }

    public void setMovimientos(List<Movimiento> movimientos) {
        this.movimientos = movimientos;
    }

    public void poner(double cuanto) {
        validarMontoPositivo(cuanto);
        validarCantidadExcepciones();
        agregarMovimiento(new Movimiento(LocalDate.now(), cuanto, true));
    }

    public void sacar(double cuanto) {
        validarMontoPositivo(cuanto);
        validarSaldoSuficiente(cuanto);
        validarLimiteExtraccion(cuanto);
        agregarMovimiento(new Movimiento(LocalDate.now(), cuanto, false));
    }

    private void validarSaldoSuficiente(double cuanto) {
        if (getSaldo() - cuanto < 0) {
            throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
        }
    }

    private void validarCantidadExcepciones() {
        if (superoMovimientosDiarios(3)) {
            throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
        }
    }

    private Boolean superoMovimientosDiarios(int cantidadPermitida) {
        return getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= cantidadPermitida;
    }

    private void validarMontoPositivo(double cuanto) {
        if (cuanto <= 0) {
            throw new MontoNegativoException(cuanto + ": el monto a ingresar debe ser un valor positivo");
        }
    }

    private void validarLimiteExtraccion(double extraccion) {
        double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
        double limite = 1000 - montoExtraidoHoy;
        if (extraccion > limite) {
            throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
                    + " diarios, límite: " + limite);
        }
    }

    public void agregarMovimiento(Movimiento movimiento) {
        setSaldo(movimiento.valorAActualizar() + getSaldo());
        movimientos.add(movimiento);
    }

    public double getMontoExtraidoA(LocalDate fecha) { //¿no debería ser private?
        return getMovimientos().stream()
                .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
                .mapToDouble(Movimiento::getMonto)
                .sum();
    }

    public List<Movimiento> getMovimientos() {
        return movimientos;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

}
