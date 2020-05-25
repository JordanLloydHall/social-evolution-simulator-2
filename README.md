# Social Evolution Simulator 2
The goal of the Social Evolution Simulator is to evolve a neural controller which exhibits social behaviour, through the simultaneous evolution of many NEAT networks in a grid environment. Each of these networks can see an object in front of them by the use of a number of ray casts, and among the input information is a 3-d vector that is the output from another neural-network-controlled entity. The environment also contains other entities (known as Resource entities, as opposed to the Actor entities which are controlled by neural networks). They exhibit simple behaviour based on their subclass of Resource entities (Edible, Tool, etc). The variables that control how the different entities behave will be optimised using a meta-optimiser, which will be optimising for the social performance of the actor entities.

## Getting Started
If you want to get the project running immediately or wish to tinker around with the code, this is the place to start!
### Prerequisites
This project has only ever been run on Ubuntu, but there is no reason that it should not work on any other other OS.
To get started, you will need JDK >11 (earlier versions may work):

    $ apt-get update && apt-get upgrade
    $ apt-get install default-jdk

### Installing
Download a clone of this repository and open up a terminal in that repository.
After that, change directory into Worker/build/:

    $ cd Worker/build
Then simply run the SocialEvoSimWorker.jar to test:

    $ java -jar SocialEvoSimWorker.jar
And that's it! You should have it up and running. Mess around with it for a while and change some values in the config.properties file using your favourite word editor to see what type of simulations you can get running. Let me know of any interesting combinations you find.
### Developing
As of now, the only IDE that has been tested and known to work with this project is Eclipse, but others may work also. In order to get started with the development environment, simply open up the Worker/ folder in Eclipse. The entry point is the worker/Gui.java class for the graphical interface and the worker/Worker.java class for a command line variant (used currently for Baysian Optimisation of the simulator's metavariables).

## Contributing
Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.
## Authors

* **Jordan Hall** - *Active Developer* - [PlatinumNinja72](https://github.com/PlatinumNinja72)

See also the list of [contributors](https://github.com/PlatinumNinja72/social-evolution-simulator-2/contributors) who participated in this project.

## License
This project is licensed under the MIT License - see the [LICENSE.md](https://gist.github.com/PurpleBooth/LICENSE.md) file for details

## Acknowledgments
* [The original Evolution Simulator](https://github.com/PlatinumNinja72/SocialEvolutionSimulator) I made back when I was about 15. It was not that good, but definitely was a fun project to write over the course of a few days and certainly made this iteration far better and taught me the importance of proper planning. 

* [Carykh's Evolv.io](https://www.youtube.com/watch?v=C9tWr1WUTuI&list=PLrUdxfaFpuuK0rj55Rhc187Tn9vvxck7t&index=11) was a simulation I found after writing my initial Social Evolution Simulator. As far as I remember, there isn't much about this project that was directly inspired by Carykh's project, but it is worth checking out because it looks quite good!
* [Textures used in the graphics of the local worker from kenney.nl](https://www.kenney.nl/assets)
* A special thanks to my secondary school physics professor, Mr. Edwards, for inspiring me to try my best at all that I do.
