# gmail-scanner
This web app scans users' gmail, gets messages and saves them to DB

The application is tested with Glassfish server. User can enter the search query and the app will scan his 
mailbox for occurences, after that relevant data is saved to database and then sent to datatable.

Project was created in Netbeans with Maven, Hibernate and JSF.

Database table creation file can be found in /resources folder under "create_message_table.sql" name.

To run the project, you will need to deploy it to GlassFish server & set up one table in the database.
