package environment;


import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import entity.Entity;
import interpreter.Interpreter;

public class WorkerThread extends Thread {

	private LinkedBlockingQueue<Entity> entityStepQueue;
	private LinkedBlockingQueue<Entity> checkSurroundingsQueue;
	private Interpreter interpreter;
	private Environment env;
	public boolean done;
	
	
	public WorkerThread(LinkedBlockingQueue<Entity> entityStepQueue, LinkedBlockingQueue<Entity> checkSurroundingsQueue, Interpreter interpreter, Environment env) {
		this.entityStepQueue = entityStepQueue;
		this.checkSurroundingsQueue = checkSurroundingsQueue;
		this.interpreter = interpreter;
		this.env = env;
		done = false;
	}
	
	@Override
	public void run() {
		Entity currentEntity;
		while (!done) {
			try {
				if (!entityStepQueue.isEmpty()) {
					currentEntity = entityStepQueue.poll(100, TimeUnit.MILLISECONDS);
					if (currentEntity != null && currentEntity.isVisable()) {
						currentEntity.onStep(interpreter, env);
					}
				} else if (!checkSurroundingsQueue.isEmpty()) {
					currentEntity = checkSurroundingsQueue.poll(100, TimeUnit.MILLISECONDS);
					if (currentEntity != null && currentEntity.isVisable()) {
						env.checkSurroundings(currentEntity);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
					
		}
	}

}
