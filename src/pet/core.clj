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

(defn db-insert
  "Insert to messages table" 
  [text]
  (sql/do-commands (str "INSERT INTO messages(name, TimeEnter) VALUES ('"text"', '" (now)"')")))

(defn db-query-for-today 
  "Query" 
  []
  (println "Records for today:")
  (sql/with-query-results rs ["select * from messages where strftime('%Y-%m-%d', TimeEnter) = strftime('%Y-%m-%d', 'now')"] 
                          (doseq [row rs] (println (:name row)))))

(defn -main [& args]
  (cmd/with-command-line 
    args
    "Commandline arguments for PET"
    [[install? b? "Choose if the database install should be run"]
     [today? b? "Find only records for today"]
     [text "The text to insert in the db"]
     remaining]
    (when install? 
      (sql/with-connection
        db
        (sql/transaction
          (db-create))))
    (when today?
      (sql/with-connection
        db
        (sql/transaction
          (db-query-for-today))))
    (when-not (st/blank? text)
      (sql/with-connection
        db
        (sql/transaction
          (db-insert text))))))

