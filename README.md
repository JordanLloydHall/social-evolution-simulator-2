# Social Evolution Simulator 2
![Image of Simulator](https://github.com/PlatinumNinja72/social-evolution-simulator-2/tree/master/Worker/SimulatorPhoto.png)

The goal of the Social Evolution Simulator is to evolve a neural controller which exhibits social behaviour, through the simultaneous evolution of many NEAT networks in a grid environment. Each of these networks can see a set of entities near them of them by the use of a number of ray casts, and among the input information is a scalar that is the output from another neural-network-controlled entity. The environment also contains other entities (known as Resource entities, as opposed to the Actor entities which are controlled by neural networks). They exhibit simple behaviour based on their subclass of Resource entities (Edible, Tool, etc). The variables that control how the different entities behave will be optimised using a meta-optimiser, which will be optimising for the social performance of the actor entities.

## Getting Started
If you want to get the project running immediately or wish to tinker around with the code, this is the place to start!
### Prerequisites
This project has only ever been run on Ubuntu, but there is no reason that it should not work on any other other OS.
To get started, you will need JDK 8 (earlier versions may work):

    $ apt-get update && apt-get upgrade
    $ apt-get install openjdk-8-jre

### Installing
Download a clone of this repository and open up a terminal in that repository.
After that, change directory into Worker/:

    $ cd Worker/
Then simply run the SocialEvoSimWorker3D.jar to test using your java 8 path:

    $ /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar SocialEvoSim3D.jar
When using the simulator, WASD is to move, 'p' toggles running the simulator, 'l' toggles updating the render, and 'esc' closes the simulator.

After running the simulator, the data.csv file will have some data that can be plotted. In order to do this easily, we use R. To run, first install the R interpreter and then run the script:

    $ R -f PlotData.r
Then you will see three plot images: InterActor.png, IntraActor.png and PopulationCounts.png. Mess around with the config.properties file and rerun the simulation to see how the affects play out!

And that's it! You should have it up and running. Mess around with it for a while and change some values in the config.properties file using your favourite word editor to see what type of simulations you can get running. Let me know of any interesting combinations you find.
### Developing
As of now, the only IDE that has been tested and known to work with this project is Eclipse, but others may work also. In order to get started with the development environment, simply open up the Worker/ folder in Eclipse. The entry point is the src/main/java/worker/Gui.java class for the graphical interface and the src/main/java/worker/Worker.java class for a command line variant (used currently for Baysian Optimisation of the simulator's metavariables).

## Contributing
Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.
## Authors

* **Jordan Hall** - *Active Developer* - [PlatinumNinja72](https://github.com/PlatinumNinja72)

See also the list of [contributors](https://github.com/PlatinumNinja72/social-evolution-simulator-2/contributors) who participated in this project.

## Acknowledgments
* [The original Evolution Simulator](https://github.com/PlatinumNinja72/SocialEvolutionSimulator) I made back when I was about 15. It was not that good, but definitely was a fun project to write over the course of a few days and certainly made this iteration far better and taught me the importance of proper planning. 

* [Carykh's Evolv.io](https://www.youtube.com/watch?v=C9tWr1WUTuI&list=PLrUdxfaFpuuK0rj55Rhc187Tn9vvxck7t&index=11) was a simulation I found after writing my initial Social Evolution Simulator. As far as I remember, there isn't much about this project that was directly inspired by Carykh's project, but it is worth checking out because it looks quite good!
* [Textures used in the graphics of the local worker from kenney.nl](https://www.kenney.nl/assets)
* A special thanks to my secondary school physics professor, Mr. Edwards, for inspiring me to try my best at all that I do.
