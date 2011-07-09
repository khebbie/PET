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

(defn db-insert [text]
  "inserts to the database" 
  (sql/do-commands (str "INSERT INTO something(name) VALUES ('"text"')")))

(defn -main [& args]
  (cmd/with-command-line 
    args
    "Command line demo"
    [[install? b? "Choose if the database install should be run"]
     [text "The text to insert in the db"]
     remaining]
    (if install? 
      (sql/with-connection
        db
        (sql/transaction
          (db-insert))))
  (println "INSERT INTO something(name) VALUES ('" text "')")
  (sql/with-connection
    db
    (sql/transaction
      (db-insert text))))
  )

