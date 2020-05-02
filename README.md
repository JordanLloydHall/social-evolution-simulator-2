
## Introduction

This is a social evolution simulator designed as a means to observe how individual automata evolve to interact with one another in a competitive and cooperative environment. The automata will have limited perception and ability in order to positively influence the creation of a social structure.


## Environment

The environment of this simulator is a discrete, two-dimensional grid in which the automata will exist. Contained within the environment are sources of resources and automata. These will be described in separate sections. The environment will have a day-night cycle, which will regulate growth of resources and influence sensory functions. The environment will be large, but finite. In order to prevent the automata from moving outside of the environment’s limits, a buffering body of water will surround it.


## Resources

The range of resources in the environment is static and limited, however the arrangement is dynamic as resources change with time and are manipulated by the automata. Resources vary along several dimensions, of which include: portability, durability, tool uses, chance of generation, chance of duplication, daylight dependence, result of action by automaton, and result of consumption by automaton. The mechanics of these properties will be described in separate sections.


### Portability

Portability dictates if an automaton can move the object. While an automaton is moving an object, it can perform no action other than placing down the object. In order for an automaton to move an object, the durability of the object must be zero (durability is described in a different section). If an automaton attempts to move an object when the object’s durability is not zero, then the object’s durability decreases by the tool level of the automaton attempting to move the object. If an object that is being moved by an automaton is placed down, its durability is reset. An object cannot be placed on top of another object. If an automaton attempts to place an object where one already exists, then the object will remain held by the automaton. Portability is a static boolean value, either being true or false.


### Durability

Durability dictates the difficulty of manipulating an object. The durability of an object at its creation is equal to the starting durability of its object type. The durability of an individual object may be changed throughout its lifetime through actions performed by automata. Durability is a variable integer greater than or equal to zero.


### Tool uses

Tool uses dictates the amount of times that a tool object may be used before it is lost. The tool use count of an object at its creation is equal to the starting tool uses of its object type. The tool uses of an individual object may be decremented throughout its lifetime through actions performed by the automaton. When tool uses become zero, the object is destroyed and no other result occurs. Tool uses may be an irrelevant property of non-tool objects. Tool uses is a variable integer greater than or equal to zero.


### Chance of generation

Chance of generation dictates the probability of an object cluster being randomly generated at the genesis of the environment. An object may not be generated where another object already exists. If the environment generator attempts to generate an object where an object already exists, the object will not be generated. An object which is never generated at the genesis of the environment will have a value of zero. Chance of generation is a static floating-point value ranging from zero to one.


### Chance of duplication

Chance of duplication dictates the probability of an object duplicating itself at each timestep and generating a new object adjacent to the original object. If the object has no empty adjacent location where a new object may be generated, then no new object is generated. An object which is never duplicated will have a value of zero. Chance of duplication is a static floating-point value ranging from zero to one.


### Daylight dependence

Daylight dependence dictates if the chance of duplication of an object varies depending on the time of day. If so, chance of duplication will be multiplied by a value of 
<img src="https://render.githubusercontent.com/render/math?math={\frac{{\cos\left(\frac{t}{T}\right)}%2B1}{2}}">
 where t is the current timestep and T is the length of the day-night cycle. Daylight dependence is a static boolean value, either being true or false.


### Result of action by automaton

Result of action by automaton is a method that exists for each object type, dictating the result of an automaton performing an action on the object. In order for the action to succeed, the durability of the object must be zero (durability is described in a different section). If an automaton attempts to perform an action on an object when the object’s durability is not zero, then the object’s durability decreases by the tool level of the automaton attempting to perform an action on it. The result of an action by an automaton may change the object type, such as processing an object into a tool object, processing wheat into wheat grain making it consumable, or it may change the properties of the object. Alternatively, the object may not exhibit any result of an action by an automaton, in which case nothing will occur.

If the resource is consumable, it will behave slightly differently. Although it uses the same method as usual. However, its role is dictating the result of an automaton consuming the object. In order for an automaton to consume an object, its durability must be zero. If an automaton attempts to consume an object when the object’s durability is not zero, then the object’s durability decreases by the tool level of the automaton attempting to consume it. The result of consumption of an automaton varies for each object type, such as increasing nutrition levels for consuming wheat grain or equipping a tool. Other than durability checks, there are no other checks relating to consumption. If wheat grain is consumed and the automaton is at maximum nutrition, then the wheat grain is lost and no meaningful result may occur. Similarly, if a tool object is consumed and the automaton currently has a better tool equipped, then the better tool is lost and the tool being consumed is equipped. 


### Resource descriptions


<table>
  <tr>
   <td>Name:
   </td>
   <td>Portability:
   </td>
   <td>Durability:
   </td>
   <td>Tool uses:
   </td>
   <td>Chance of generation:
   </td>
   <td>Chance of duplication:
   </td>
   <td>Daylight dependence:
   </td>
   <td>Result of action by automaton:
   </td>
  </tr>
  <tr>
   <td>Wheat
   </td>
   <td>T
   </td>
   <td>2
   </td>
   <td>0
   </td>
   <td>0.01
   </td>
   <td>0.01
   </td>
   <td>T
   </td>
   <td>Transformed into wheat grain.
   </td>
  </tr>
  <tr>
   <td>Wheat grain
   </td>
   <td>T
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Increases nourishment level of automaton.
   </td>
  </tr>
  <tr>
   <td>Meat
   </td>
   <td>T
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Increases nourishment level of automaton.
   </td>
  </tr>
  <tr>
   <td>Water
   </td>
   <td>F
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>None
   </td>
  </tr>
  <tr>
   <td>Sapling
   </td>
   <td>T
   </td>
   <td>4
   </td>
   <td>0
   </td>
   <td>0.005
   </td>
   <td>0.005
   </td>
   <td>T
   </td>
   <td>Transformed into wood.
   </td>
  </tr>
  <tr>
   <td>Wood
   </td>
   <td>T
   </td>
   <td>8
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Transformed into wood tool.
   </td>
  </tr>
  <tr>
   <td>Wood tool
   </td>
   <td>T
   </td>
   <td>0
   </td>
   <td>32
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Tool is equipped by automaton. Tool level is 2.
   </td>
  </tr>
  <tr>
   <td>Stone
   </td>
   <td>T
   </td>
   <td>8
   </td>
   <td>0
   </td>
   <td>0.001
   </td>
   <td>0.001
   </td>
   <td>F
   </td>
   <td>Transformed into cobble.
   </td>
  </tr>
  <tr>
   <td>Cobble
   </td>
   <td>T
   </td>
   <td>16
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Transformed into stone tool.
   </td>
  </tr>
  <tr>
   <td>Stone tool
   </td>
   <td>T
   </td>
   <td>0
   </td>
   <td>64
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Tool is equipped by automaton. Tool level is 4.
   </td>
  </tr>
  <tr>
   <td>Iron ore
   </td>
   <td>T
   </td>
   <td>16
   </td>
   <td>0
   </td>
   <td>0.0005
   </td>
   <td>0.0005
   </td>
   <td>F
   </td>
   <td>Transformed into iron.
   </td>
  </tr>
  <tr>
   <td>Iron
   </td>
   <td>T
   </td>
   <td>32
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Transformed into iron tool.
   </td>
  </tr>
  <tr>
   <td>Iron tool
   </td>
   <td>T
   </td>
   <td>0
   </td>
   <td>128
   </td>
   <td>0
   </td>
   <td>0
   </td>
   <td>F
   </td>
   <td>Tool is equipped by automaton. Tool level is 8.
   </td>
  </tr>
</table>



## Automata

Automata are the agents which act in the environment. The implicit objective of these agents is to replicate, so that their genetic information will be preserved over time. The automata will be subject to variation within a population and speciation caused by the isolation of a population of species over time. Automata will have properties that are inherited from two parent automata and are subject to the mutation of properties at generation. The automata can exhibit behaviours that affect the environment, such as: moving, rotating, reproducing, consuming, performing an action, communicating, and attacking. These will be described in separate sections. The behaviour of an automaton will be controlled by a neural network. using a modified Neuro-Evolution of Augmenting Topologies (NEAT) algorithm (Stanley and Miikkulainen, 2002). Automata may die from external factors or old age. When automata die, meat will be dropped in the terminating position.


### Movement

An automaton can move in one of four directions or it may rotate left or right every timestep if it is not performing any other action. Moving in a direction will decrease the nourishment level of the automata by a set amount, which is multiplied by 1.5x when holding an object and then multiplied again by 1.25x if gestating an automaton. Rotating will not decrease the nourishment of the automata.


### Reproduction

If an automaton that chooses to reproduce is in an adjacent location to another automaton and is facing the other automaton, then reproduction will occur. When this occurs, the genetic information of the two automata will be subject to crossover and mutation as described in the NEAT algorithm. The automaton that chose to reproduce will gestate the new automaton for a period of time before the new automaton is generated in a free adjacent location. If there is no free adjacent location, then gestation continues until a free adjacent location is available. If the parent automaton dies before gestation ends, then the new automaton will not be generated.


### Properties of automata

An automaton will contain some properties inherited from their parent automata that are subject to mutation. These properties include a field of view constant (in perception section) and a controller neural network (in control section).


### Actions

Automata can perform an action on an object, the result of which varies depending on which object the action was performed on. Some objects will consumed or equipped when an action is performed on them (see the resources section for effects of actions on objects). When an action is performed on another automaton, that automaton will be attacked and the damage dealt will be proportional to the tool level of the attacking automaton.

*Illustration of an automaton transforming wheat into wheat grain*

*Illustration of an automaton transforming wood into a wooden tool*


### Communication

As mentioned in the perception section, each raycast has a “state” channel, which returns either the percentage of remaining durability of an object or the communication output of an automaton. The communication output of an automaton is determined by outputs from the controller.

Note that the communication output is limited between zero and one. This is to prevent an automaton over-stimulating a neighboring automaton by allowing a large input into the receiving neural network and possibly disrupting the internal processes of the automaton.
