package worker;

import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import entity.Actor;
import entity.Wheat;
import environment.Environment;
import interpreter.Interpreter;

public class Worker {

//	public static void main(String[] args) throws IOException {
//		Properties properties = PropertyFileReader.getProperties();
//		Interpreter interpreter = new Interpreter(properties);
//		Environment env = new Environment(properties, interpreter);
//		env.resetWorld();
//		Wheat newWheat = new Wheat(properties, new Point(1,0));
//		Actor newactor = new Actor(properties, new Point(1,1));
//		env.insertEntity(newWheat, 1, 0);
//		env.insertEntity(newactor, 1, 1);
//		
//		for (int i=0; i<5; i++) {
//			System.out.println(Arrays.deepToString(env.getWorldGrid()));
//			System.out.println(newactor.getHeldResource());
//			env.step();
//		}
//		
//	}

}
