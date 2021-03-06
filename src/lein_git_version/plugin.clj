(ns lein-git-version.plugin
  (:require
   [clojure.pprint]
   [clojure.string :as str]
   [leiningen.git-version :refer [get-git-ref get-git-last-message]]))

(defn middleware
  [project]
  (let [code (str
              ";; Do not edit.  Generated by lein-git-version plugin.\n"
              "(ns " (if-let [path (:git-version-path project)]
                       (-> path
                           (str/replace (:root project) "")
                           (str/replace #"^/src/" "")
                           (str/replace "/" "."))
                       (:name project)) ".version)\n"
              "(def version \"" (:version project) "\")\n"
              "(def gitref \"" (get-git-ref) "\")\n"
              "(def gitmsg \"" (get-git-last-message) "\")\n")
        proj-dir (.toLowerCase (.replace (:name project) \- \_))
        filename (if (:git-version-path project)
                   (str (:git-version-path project) "/version.clj")
                   (str (or (first (:source-paths project)) "src") "/"
                        proj-dir "/version.clj"))]
    (spit filename code)
    project))
