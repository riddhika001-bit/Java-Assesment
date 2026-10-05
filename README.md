# Java Assessment
### Assessment 1
[write a java code to store the population of India and China and print the population](.java)

### Assessment 2
[write a java code to calculate the area of circle](.java)

### Assessment 3
[write a java code to assign grade A for the student who have the marks above 90 check if a student has passed the exam or not(pass marksis 70)](.java)

### Assessment 4
[java code for simple calculator](.java)

### Assessment 5
[find the sum and average of the array in java](.java)

### Assessment 6
[java code for adding rows in matrix](.java)

### Assessment 7
[write a code by using 3 methods of string in java](.java)

### Assessment 8
[write  a java code by spliting a sentence  into word and then rebuilt it in new forma](.java)

### Assessment 9
[java code for fibonacci with recursion](.java)

### Assessment 10
[write java code for selection sort and insertion sor](.java)

### Assessment 11
[java code for counting vowels in string](.java)

### Assessment 12
[java code for reversing an array in place](.java)

### Assessment 13
[java code for 2nd largest element](.java)

### Assessment 14
[write a java code to create hierarchy with class animal subclass dog,forrabbit](.java)

### Assessment 15
[write java code for method overidding a string where each class inherts to string from object and overiddibg that to see how the object can be printed](.java)

### Assessment 16
[write a java code to implement the abraction by using shapes and 2 sub classes which can have the fuctionality in different ways](.java)

### Assessment 17
[java code for managing a To Do list adding removing and iterating over a simple arraylist of tasks](.java)

### Assessment 18
[java code for accessing and removing elements in a linkedlist by using its operations](.java)

### Assessment 19
[write a java code by using try,catch,finally,block for any arthimetic exception or array index out of bound exception](.java)

### Assessment 20
[java code for finding the largest element in an array](.java)

### Assessment 21
[java code for create a class which can shared by two objects(student) for name and marks in a subject](.java)

### Assessment 22
[given an array of integers return the number of distinct absolute values among the elements of the array absolute of any value is defined as its positive equivalent ABS(-5)=505 MATHEMATICALLY |-5|=|5|=1](.java)

### Assessment 23
[GIVEN AN ARRAY OF INTEGERS AND AN INTEGER TARGET PRINT INDIES OF THE TWO numbers such thst the numbers add up to target you may assume that each input would have exactly one solution and you may not the use elemnt tewce you must print the answer indices in ascending order ifno such pair exits return -1,1](.java)

### Assessment 24
[java code for you are given N string of length M count the number of anagramic groups](.java)

### Assessment 25
[Write a SQL queue for creating a students table which has roll no,name,age,date of birth,email ID,phone number and address and the primary keys are students ID,name,email ID and phone number should not be null and insert any three records into the table](sql_queue.sql)



(readme file for mini project)


# Hospital Management System

A Java console application for managing hospital patients, treatment queues, and doctor assignments. Patient and doctor records are stored in MySQL.

## Features

- Add, view, search, update, sort, and discharge patients
- Prioritize patients in the treatment queue by severity
- Undo the most recent discharge
- Assign doctors by department using round-robin rotation and view doctor workloads
- Create the database and tables automatically when the application starts

## Requirements

- Java Development Kit (JDK)
- MySQL Server running locally on port `3306`
- MySQL Connector/J
- Visual Studio Code with the Extension Pack for Java (optional)

## Setup

1. Clone or download this repository.
2. Place the MySQL Connector/J `.jar` file in the project's `lib` directory. The VS Code project settings load JAR files from this directory.
3. Set the database credentials in your environment. The application reads `HOSPITAL_DB_USER` and `HOSPITAL_DB_PASSWORD`; it does not contain a database password.

   In PowerShell, for the current terminal:

   ```powershell
   $env:HOSPITAL_DB_USER = "your-mysql-username"
   $env:HOSPITAL_DB_PASSWORD = "your-mysql-password"
   ```

4. Open the project folder in VS Code and run `Main.java`.

On startup, the application creates the `hospital_management` database and the required tables if they do not exist. The MySQL account must have permission to create databases and tables.

## Project layout

```text
src/        Java source files
lib/        MySQL Connector/J dependency (add locally)
bin/        Compiled output (generated; not committed)
```
