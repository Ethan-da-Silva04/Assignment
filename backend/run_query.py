import sys
import os

if __name__ == "__main__":
    sql_file_name = sys.argv[1]
    db_name = "Donations"
    os.system("sudo mysql {database_name} < {sql_file_name}".format(database_name = db_name, sql_file_name = sql_file_name))
