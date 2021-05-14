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
    private List<Movimiento> extracciones = new ArrayList<>();
    private List<Movimiento> depositos = new ArrayList<>();


    public Cuenta() {
        saldo = 0;
    }

    public Cuenta(double montoInicial) {
        saldo = montoInicial;
    }

    public void poner(double cuanto) {
        validarMontoPositivo(cuanto);
        validarCantidadExcepciones();
        agregarMovimiento(new Deposito(LocalDate.now(), cuanto), depositos);
    }

    public void sacar(double cuanto) {
        validarMontoPositivo(cuanto);
        validarSaldoSuficiente(cuanto);
        validarLimiteExtraccion(cuanto);
        agregarMovimiento(new Extraccion(LocalDate.now(), cuanto), extracciones);
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
        return getDepositos().stream().count() >= cantidadPermitida;
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

    public void agregarMovimiento(Movimiento movimiento, List <Movimiento> lista) {
        setSaldo(movimiento.valorAActualizar() + getSaldo());
        lista.add(movimiento);
    }

    public double getMontoExtraidoA(LocalDate fecha) { //¿no debería ser private?
        return getExtracciones().stream()
                .filter(extraccion -> extraccion.esDeLaFecha(fecha)) //esto antes usaba getFecha -> rompia encapsulamiento
                .mapToDouble(Movimiento::getMonto)
                .sum();
    }

    public List<Movimiento> getExtracciones() {
        return extracciones;
    }

    public List<Movimiento> getDepositos() {
        return depositos;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

}
