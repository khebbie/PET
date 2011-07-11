(ns pet.core
  (:require [clojure.contrib.sql :as sql])
  (:require [clojure.contrib.command-line :as cmd])
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

(defn -main [& args]
  (cmd/with-command-line 
    args
    "Commandline arguments for PET"
    [[install? b? "Choose if the database install should be run"]
     [text "The text to insert in the db"]
     remaining]
    (if install? 
      (sql/with-connection
        db
        (sql/transaction
          (db-create))))
    (comment(sql/with-connection
      db
      (sql/transaction
        (db-create))))
    (println (now))
  (sql/with-connection
    db
    (sql/transaction
      (db-insert text))))
  )

