(ns pallet.crate.etc-default-test
  (:require
   [clojure.test :refer :all]
   [pallet.action :refer [action-fn]]
   [pallet.actions :refer [remote-file]]
   [pallet.actions.decl :refer [remote-file-action]]
   [pallet.build-actions :refer [build-plan]]
   [pallet.common.logging.logutils :refer [logging-threshold-fixture]]
   [pallet.crate.etc-default :as default]
   [pallet.plan :refer [plan-context]]
   [pallet.test-utils
    :refer [with-bash-script-language
            with-ubuntu-script-template]]))

(use-fixtures :once
              with-ubuntu-script-template with-bash-script-language
              (logging-threshold-fixture))

(def remote-file* (action-fn remote-file-action :direct))

(deftest default-test
  (is (=
       (build-plan [session {}]
         (plan-context write {}
           (remote-file
            session
            "/etc/default/tomcat6"
            {:owner "root"
             :group "root"
             :mode 644
             :content
             "JAVA_OPTS=\"-Djava.awt.headless=true -Xmx1024m\"\nJSP_COMPILER=\"javac\""})))
       (build-plan [session {}]
         (default/write
           session
           "tomcat6"
           :JAVA_OPTS "-Djava.awt.headless=true -Xmx1024m"
           "JSP_COMPILER" "javac"))))
  (is (=
       (build-plan [session {}]
         (plan-context write {}
           (remote-file
            session
            "/etc/tomcat/tomcat6"
            {:owner "root"
             :group "root"
             :mode 644
             :content "JAVA_OPTS=\"-Djava.awt.headless=true\""})))
       (build-plan [session {}]
         (default/write
           session
           "/etc/tomcat/tomcat6"
           :JAVA_OPTS "-Djava.awt.headless=true")))))
