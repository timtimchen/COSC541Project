library(openxlsx)
library(stats)
data <- read.xlsx("Analysis.xlsx",colNames = FALSE, sheet="Accuracy")
testdata <- t(data)
#data_values <- cbind(data[1,], data[2,], data[3,], data[4,], data[5,], data[6,], data[7,], data[8,])
data_values <- c(data[,1], data[,2], data[,3], data[,4], data[,5], data[,6], data[,7], data[,8])
data_class <- c(rep('technique1', 8),rep('technique2', 8),rep('technique3', 8),rep('technique4', 8),rep('technique5', 8),rep('technique6', 8),rep('technique7', 8),rep('technique8', 8))
data_class
data_values
combine <- cbind((data_values), data_class)
results <- aov((data_values) ~ data_class)
anova(results)
boxplot((data_values) ~ data_class)
TukeyHSD(results, conf.level=0.95)
