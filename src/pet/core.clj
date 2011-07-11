(ns pet.core
  (:require [clojure.contrib.sql :as sql])
  (:require [clojure.contrib.command-line :as cmd])
  (:require [clojure.string :as st])
  (:use [clj-time.core])
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
    :messages
    [:id :integer "PRIMARY KEY AUTOINCREMENT"]
    [:name "varchar(32)"]
    [:TimeEnter "DATE"]))

(defn db-insert [text]
  "inserts to the database"
  (sql/do-commands (str "INSERT INTO messages(name, TimeEnter) VALUES ('"text"', '" (now)"')")))

(defn db-query-for-today []
  "Query records for today"
  (println "Records for today")
  (sql/with-query-results rs ["select * from messages;"] 
               (doseq [row rs] (println (:name row)))
                      ))
  
(defn -main [& args]
  (cmd/with-command-line 
    args
    "Commandline arguments for PET"
    [[install? b? "Choose if the database install should be run"]
     [today? b? "Find only records for today"]
     [text "The text to insert in the db"]
     remaining]
    (if install? 
      (sql/with-connection
        db
        (sql/transaction
          (db-create)))
      )
    (if today?
      (sql/with-connection
        db
        (sql/transaction
          (db-query-for-today)))
      )
    (if-not (st/blank? text)
      (sql/with-connection
        db
        (sql/transaction
          (db-insert text)))))
  )

