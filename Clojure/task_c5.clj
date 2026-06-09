(ns task-c5)

(defn fork [id]
  (ref {:id id :uses 0 :taken false}))

(defn order-forks [idx left right]
  (if (even? idx) [left right] [right left]))

(defn take-forks [{:keys [restart-counter]} idx left right]
  (loop []
    (let [take
          (dosync
            (let [[f s] (order-forks idx left right)]
              (if (or (:taken @f) (:taken @s))
                :retry
                (do (alter f assoc :taken true)
                    (alter s assoc :taken true)
                    :ok))))]
      (if (= take :ok)
        :ok
        (do (swap! restart-counter inc)
            (Thread/yield)
            (recur))))))

(defn put-forks [left right]
  (dosync
    (alter left  (fn [st] (-> st (assoc :taken false) (update :uses inc))))
    (alter right (fn [st] (-> st (assoc :taken false) (update :uses inc))))))

(defn philosopher-loop
  [{:keys [idx iterations think-ms eat-ms forks restart-counter]}]
  (let [n (count forks)
        left  (forks idx)
        right (forks (mod (inc idx) n))]
    (loop [rem iterations meals 0]
      (if (zero? rem)
        {:philosopher idx :meals meals}
        (do
          (when (pos? think-ms) (Thread/sleep think-ms))
          (take-forks {:restart-counter restart-counter} idx left right)
          (when (pos? eat-ms) (Thread/sleep eat-ms))
          (put-forks left right)
          (recur (dec rem) (inc meals)))))))

(defn dining
  [{:keys [philosophers iterations think-ms eat-ms timeout-ms]}]
  (let [forks (vec (map fork (range philosophers)))
        restart-counter (atom 0)
        start-promise (promise)
        workers (mapv (fn [idx]
                        (future
                          @start-promise
                          (philosopher-loop {:idx idx :iterations iterations
                                             :think-ms think-ms :eat-ms eat-ms
                                             :forks forks :restart-counter restart-counter})))
                      (range philosophers))
        startTime (System/nanoTime)]
    (deliver start-promise true)
    (let [deadline (when timeout-ms (+ (System/currentTimeMillis) timeout-ms))
          results (loop [ws workers acc []]
                    (if (empty? ws)
                      acc
                      (let [w (first ws)
                            waitTime (when deadline (max 1 (- deadline (System/currentTimeMillis))))
                            v (deref w waitTime ::timeout)]
                        (if (= v ::timeout)
                          (do (doseq [x ws] (future-cancel x)) ::timeout)
                          (recur (rest ws) (conj acc v))))))
          duration-ms (/ (- (System/nanoTime) startTime) 1e6)]
      (if (= results ::timeout)
        {:status :timeout
         :duration-ms duration-ms
         :restart-count @restart-counter
         :args {:philosophers philosophers
                :iterations iterations
                :think-ms think-ms
                :eat-ms eat-ms
                :timeout-ms timeout-ms}}
        (let [meals (vec results)
              total (reduce + (map :meals meals))]
          {:status :completed
           :duration-ms duration-ms
           :restart-count @restart-counter
           :total-meals total
           :args {:philosophers philosophers
                  :iterations iterations
                  :think-ms think-ms
                  :eat-ms eat-ms
                  :timeout-ms timeout-ms}})))))

(defn print-results
  [{:keys [args status duration-ms restart-count]}]
  (println "--------------------------------------------")
  (println (format "Результаты запуска с параметрами %s:" (pr-str args)))
  (println "--------------------------------------------")
  (println (format "Статус завершения: %s" (name status)))
  (println (format "Время выполнения: %.2f ms" duration-ms))
  (println (format "Перезапусков транзакций: %d" restart-count)))

(defn start []
    (print-results (dining {:philosophers 6
                          :iterations 20
                          :think-ms 1
                          :eat-ms 5
                          :timeout-ms 1000}))
    (shutdown-agents))

(start)