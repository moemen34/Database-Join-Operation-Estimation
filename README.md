# Database-Join-Operation-Estimation
## Description:
This java program connects to a PostgreSQL Database, takes the names of two schemas as arguments, and returns an estimation of the number of tuples resulting from joining them using "NATURAL JOIN".
In addition to the **Estimated Join Size**, the program returns the **Actual Join Size** and an **Estimation Error** (*Estimated Join Size
− Actual Join Size*).

## How to Run and Dependencies:
### Needed Components:
- PostgreSQL server.
- PostgreSQL JDBC driver: The .jar file needs to be accessible when compiling your code. If it
is not in the path and you are using the Eclipse IDE, you can add it to your project by going
to *Project > Properties > Java Build Path > Add External JARs* and selecting the .jar file.
- pgAdmin: For manually exploring your databases.

### Running the program:
- Modify *ConnectToPgJDBC.java* file/class to include your corresponding Database **URL**, **UserName**, and **Password**.
- Compile and Run the program with 2 arguments (*names of the tables to join*).
