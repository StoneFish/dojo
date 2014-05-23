(ns samples.Wishlist)

(defn add-item [wishlist item]
  (dosync (alter wishlist conj item)))

(def family-wishlist (ref '("ipad")))

(def original-wishlist (ref @family-wishlist))

(println "Original wish list is " @original-wishlist)

(future (add-item family-wishlist "MBP"))
(future (add-item family-wishlist "Bike"))

(. Thread sleep 1000)

(println "Original wish list is" @original-wishlist)
(println "Update wish list is" @family-wishlist)
