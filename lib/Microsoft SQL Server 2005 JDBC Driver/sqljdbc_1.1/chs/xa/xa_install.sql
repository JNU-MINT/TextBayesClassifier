-- This script installs the extended stored procedures that implement
-- distributed transaction and XA support for the Microsoft SQL Server 2005 JDBC Driver.

-- Notes for SQL Administrators:

-- #1. Prior to running this script you must copy the extended stored procedure dll SQLJDBC_XA.dll 
--     to the target SQL Server's Binn folder.

-- #2. Permissions to the distributed transaction support procedures for the Microsoft SQL Server 2005 
--     JDBC Driver are granted through the SQL Server role [SqlJDBCXAUser].  To maintain a secure default 
--     configuration, no user is granted access to this role by default.

-- Drop and re-create the extended stored procedure definitions in master.

use master
go

-- Drop any existing procedure definitions.
exec sp_dropextendedproc 'xp_sqljdbc_xa_init' 
exec sp_dropextendedproc 'xp_sqljdbc_xa_start'
exec sp_dropextendedproc 'xp_sqljdbc_xa_end'
exec sp_dropextendedproc 'xp_sqljdbc_xa_prepare'
exec sp_dropextendedproc 'xp_sqljdbc_xa_commit'
exec sp_dropextendedproc 'xp_sqljdbc_xa_rollback'
exec sp_dropextendedproc 'xp_sqljdbc_xa_forget'
exec sp_dropextendedproc 'xp_sqljdbc_xa_recover'
go

-- Install the procedures.
exec sp_addextendedproc 'xp_sqljdbc_xa_init', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_start', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_end', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_prepare', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_commit', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_rollback', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_forget', 'SQLJDBC_XA.dll'
exec sp_addextendedproc 'xp_sqljdbc_xa_recover', 'SQLJDBC_XA.dll'
go

-- Create the [SqlJDBCXAUser] role in master database.
-- The SQL administrator can later add users to this role to allow users to participate 
-- in Microsoft SQL Server 2005 JDBC Driver distributed transactions.
sp_addrole [SqlJDBCXAUser]
go

-- Grant privileges to SQLJDBC2000 role to the extended stored procedures.
grant execute on xp_sqljdbc_xa_init to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_start to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_end to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_prepare to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_commit to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_rollback to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_recover to [SqlJDBCXAUser]
grant execute on xp_sqljdbc_xa_forget to [SqlJDBCXAUser]
go

-- Add users to the [SqlJDBCXAUser] role as needed.

-- Example for adding a SQL authentication user to the SqlJDBCXAUser role.
-- exec sp_addrolemember [SqlJDBCXAUser], 'MySQLUser'

-- Example for adding a windows domain user to the SqlJDBCXAUser role.
-- exec sp_addrolemember [SqlJDBCXAUser], 'MyDomain\MyWindowsUser'

print ''
print 'SQLJDBC XA DLL installation script complete.'
print 'Check for any error messages generated above.'