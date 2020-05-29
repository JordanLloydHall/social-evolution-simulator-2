package main.java.worker;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import main.java.environment.Environment;
import main.java.interpreter.Interpreter;

public class Worker {

	public static void main(String[] args) throws IOException {
		Properties properties;
		Interpreter interpreter;
		Environment environment;
		properties = PropertyFileReader.getProperties();
		interpreter = new Interpreter(properties);
		environment = new Environment(properties, interpreter, new Random());

		boolean finished = false;
		int timeSteps = 0;
		while (!finished) {
        	finished = environment.step(timeSteps);
        	timeSteps += 1;

		}
		return;
		
	}

}
