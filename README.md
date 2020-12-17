# Firebird Embedded library

This a Firebird SQL library to provide the instalation files for Firebird SQL embedded.

With this library, o can just set up a java application using a Firebird SQL Embedded database.

## How to use

**Prerequisites**

- [JDK 8 or later](https://www.oracle.com/br/java/technologies/javase-downloads.html)
- [Apache Maven](https://maven.apache.org/) - installed in your path to be able to use `mvn` command
- [Git SCM](https://git-scm.com/)

**Packaging** 

Open the Git Bash (windows) or a terminal (linux) and type:

```
# clone it!
git clone https://github.com/betonetotbo/firebirdsql-embedded.git
# install the library locally
cd firebirdsql-embedded
cd firebird-embedded
mvn install
``` 

**Using**

In your project (maven) add this dependency:

```
<dependency>
	<groupId>br.com.jjw.firebird-embedded</groupId>
	<artifactId>firebird-embedded</artifactId>
	<version>3.0.7.33374</version>
</dependency>
```

## Example

This demo needs the jaybird maven dependency:

```
<dependency>
	<groupId>org.firebirdsql.jdbc</groupId>
	<artifactId>jaybird</artifactId>
	<version>4.0.1.java8</version>
</dependency>
```

Code:

```java
package firebirsql.emdeded;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.firebirdsql.gds.impl.GDSType;
import org.firebirdsql.management.FBManager;

import br.com.jjw.firebird.embedded.service.FirebirdEmbeddedServiceProvider;

public class Demo {
	
	private static void createDatabase(String filename) {
		GDSType type = GDSType.getType("EMBEDDED");
		try (FBManager fbManager = new FBManager(type)) {
			fbManager.start();
			fbManager.createDatabase(filename, null, null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static void main(String[] args) throws IOException, SQLException {
		FirebirdEmbeddedServiceProvider.getInstance().install(Paths.get("path-to-install"));
		createDatabase("demo.fdb");
		try (Connection conn = DriverManager.getConnection("jdbc:firebirdsql:embedded:demo.fdb")) {
			// sql queries (insert, create table, delete, select....)
		}
	}

}

```