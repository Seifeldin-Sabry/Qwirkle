# Project Qwirkle - Integration 1.2

Team Members: Seifeldin, Sakis and Nathan


## Steps to run

1. check that you have these items
    - Java Development Kit Version 17+
    - JavaFX Library (SDK): if not go to [gluonhq](https://gluonhq.com/products/javafx/) and download V18.0.1 for your operating system
    - Postgres Database: follow the instructions until 4:14 in [this video](https://www.youtube.com/watch?v=Girj-TtS60I&t=20s)

### Extract and Include the JavaFX library in Intellij
1. Extract the download from earlier in a reliable folder where it won't be deleted
2. Open the extracted game folder in Intellij
3. Go to the File > Project Structure
4. Go to Global Libraries and click the '+' icon
5. Click on 'Java'
6. Navigate to the location of the extracted SDK
7. Inside that folder, there is a 'lib' folder select it and click open
8. Once Added, right click on it and 'Add to Modules'

### Add Postgres Driver as Library
1. In lib folder inside game folder, find 'postgresql-42.3.3.jar' (make sure you do this in Intellij)
2. Right-click on that file and scroll till you find 'Add as Library'
3. Select this option and you're ready

### Run the game
1. Click the Green Button and enjoy
