# install.packages("smooth")
require(pracma)

data <- read.csv(file = 'data.csv')



png("InterActor.png", width=800,height=1600, res=150)
par(mfrow=c(4,1))
a <- movavg(data$homicides, 250, type="s")/movavg(data$deaths, 250, type="s")
b <- movavg(data$lifeSpan/data$numActors, 250, type="s")
c <- movavg(data$sexual, 250, type="s")/movavg(data$sexual+data$asexual, 250, type="s")
plot(data$ts, a*100, type="l", col="green", xlab="Time", ylab="Homicide Rate (% of deaths)")
plot(data$ts, c*100, type="l", col="brown", xlab="Time", ylab="Sexual Reproduction (% of births)")
plot(data$ts, movavg(data$genDistAvg, 250, type="s"), type="l", col="blue", xlab="Time", ylab="Average Genetic Distance")
plot(data$ts, data$numberOfSpecies, type="l", col="blue", xlab="Time", ylab="Number of Species")


png("IntraActor.png", width=800,height=2400, res=150)
par(mfrow=c(6,1))
plot(data$ts, b, type="l", col="purple", xlab="Time", ylab="Average Lifespan (timesteps)")
plot(data$ts, data$fovAvg, type="l", col="red", xlab="Time", ylab="Fov (rads)")
plot(data$ts, data$viewAvg, type="l", col="blue", xlab="Time", ylab="Average View Radius")
plot(data$ts, data$totalSize/data$numActors, type="l", col="green", xlab="Time", ylab="Average Size")
plot(data$ts, data$totalSpeed/data$numActors, type="l", col="black", xlab="Time", ylab="Average Speed")
plot(data$ts, data$gestationCost/data$numActors, type="l", col="orange", xlab="Time", ylab="Average Gestation Cost")


png("PopulationCounts.png", width=800,height=1200, res=150)
par(mfrow=c(3,1))
plot(data$ts, data$numActors, type="l", col="red", xlab="Time", ylab="Actors")
plot(data$ts, data$numWheat, type="l", col="orange", xlab="Time", ylab="Wheat")
plot(data$ts, data$numTrees, type="l", col="green", xlab="Time", ylab="Trees")


