(ns lucid.publish.link.tag
  (:require [hara.string.case :as case]))

(def tag-required?
  #{:chapter :section :subsection :subsubsection :appendix :citation})

(def tag-optional?
  #{:ns :reference :image :equation})

(defn inc-candidate
  ""
  [candidate]
  (if-let [[_ counter] (re-find #"-(\d+)$" candidate)]
    (let [len (count counter)
          num (Long/parseLong counter)]
      (str (subs candidate 0 (- (count candidate) len)) (inc num)))
    (str candidate "-0")))

(defn tag-string
  ""
  [s]
  (-> (case/spear-case s)
      (.replaceAll "\\." "-")
      (.replaceAll "/" "--")
      (.replaceAll "[^\\d^\\w^-]" "")))

(defn create-candidate
  ""
  [{:keys [origin title type] :as element}]
  (cond origin
        (case origin
          :ns (tag-string (str "ns-" (:ns element)))
          :reference (tag-string (str (name (:mode element)) "-" (:refer element))))

        title
        (tag-string title)

        (= :image type)
        (tag-string (str "img-" (:src element)))))

(defn create-tag
  ""
  ([element tags]
   (create-tag element tags (create-candidate element)))
  ([element tags candidate]
   (cond (nil? candidate)
         element

         (get @tags candidate)
         (create-tag element tags (inc-candidate candidate))

         :else
         (do (swap! tags conj candidate)
             (assoc element :tag candidate)))))

(defn link-tags
  ""
  [{:keys [articles] :as interim} name]
  (let [tags (atom (get-in articles [name :tags]))]
    (let [auto-tag (->> (list (get-in articles [name :link :auto-tag])
                              (get-in interim [:meta :link :auto-tag])
                              true)
                        (drop-while nil?)
                        (first))
          auto-tag (cond (set? auto-tag) auto-tag
                         (false? auto-tag) #{}
                         (true? auto-tag) tag-optional?)]
      (->> (get-in articles [name :elements])
           (mapv (fn [element]
                     (cond (and (or (tag-required? (:type element))
                                    (auto-tag      (:type element))
                                    (auto-tag      (:origin element)))
                                (nil? (:tag element))
                                (not  (:hidden element)))
                           (create-tag element tags)

                           :else element)))
           (assoc-in interim [:articles name :elements])))))
