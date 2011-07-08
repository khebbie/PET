(ns pet.core
  (:require [clojure.contrib.sql :as sql])
  (:require [clojure.contrib.command-line :as cmd])
  (:gen-class))


(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"         ; Protocol to use
         :subname "db/db.sqlite3"      ; Location of db
         :create true})

(. Class (forName "org.sqlite.JDBC")) ; Initialize the JDBC driver

(defn db-create 
  "Creates the table for this model"
  []
  (sql/create-table
    :something
    [:id :int "PRIMARY KEY"]
    [:name "varchar(32)"]))

(defn db-insert
  "inserts to the database" 
  []
  (sql/do-commands "INSERT INTO something(name) VALUES ('khebbie')"))

(defn -main [& args]
  (cmd/with-command-line args
                         "Command line demo"
                         [[install? b? "Choose if the database install should be run"]
                          remaining]
                         (if (or install? false)
                           (sql/with-connection
                             db
                             (sql/transaction
                               (db-insert))))

                         )

  (sql/with-connection
    db
    (sql/transaction
      (db-insert))))

