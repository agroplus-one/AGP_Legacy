package com.rsi.agp.batch.comisiones.util;

import java.util.TimerTask;

public class ExecutionTimer extends TimerTask {

	public void run() {
		System.err.println("Timeout excedido. Saliendo.");
		System.exit(6);
	}
	
}
