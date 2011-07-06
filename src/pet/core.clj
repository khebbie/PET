(ns pet.core
  (:require [clojure.contrib.sql :as sql])
  (:gen-class))


(def db {:classname "org.sqlite.JDBC"
         :subprotocol "sqlite"         ; Protocol to use
         :subname "db/db.sqlite3"      ; Location of db
         :create true})

(. Class (forName "org.sqlite.JDBC")) ; Initialize the JDBC driver

;(defn db-create 
; "Creates the table for this model"
;  []
;  (sql/create-table
;   :something
;   [:id :int "PRIMARY KEY"]
;   [:name "varchar(32)"]))

(defn db-insert
  "inserts to the database" 
   []
   (sql/do-commands "INSERT INTO something(id,name) VALUES (1,'khebbie')"))

(defn -main []

(sql/with-connection
 db
 (sql/transaction
  (db-insert))))

