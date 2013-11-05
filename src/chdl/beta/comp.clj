(ns chdl.beta.comp
  "Components"
  (:require [chdl.alpha.literal :as lit]
            [chdl.alpha.expr :as expr]
            [chdl.alpha.proto :as proto]))

(defn do-statements 
  "Given statements, return a new literal that combines them all 
  with a semicolon and newline at the end of every statement"
  [& statements] 
  (apply expr/concated
    (for [s statements]
      (expr/newlined (expr/semicolond s)))))

(defn paren-call
  "Given a function call (or whatever, it's vhdl) and arguments, represents the
  'func(arg1,arg2,arg3)' syntax"
  [func & args]
  (expr/concated
    (lit/raw func)
    (expr/parend (apply expr/comma-sepd args))))

(defn downto
  "Given two numeric literals a and b, represents the 'a downto b' syntax"
  [a b]
  (expr/space-sepd a (lit/raw :DOWNTO) b))

(defn array-of
  "Given an array type (for example, :bit) and at least one downto statement
  (more than one for a multi-dimensional array), represents the 'array(a downto
  b) of :type' syntax"
  [typ dt0 & dt]
  (expr/space-sepd
    (apply paren-call :ARRAY dt0 dt)
    (lit/raw :OF)
    (lit/raw typ)))

(defn array
  "Given one or more literals (or other array expressions, as would be used with
  a multi-level array), represents the '(\"00\",\"01\",\"10\")' syntax"
  [e0 & e]
  (expr/parend (apply expr/comma-sepd e0 e)))

(defn others
  "Given some literal (or possibly another others expression, as would be used
  with a multi-level array), represents the '(OTHERS => :somebit)' syntax"
  [b]
  (expr/parend (expr/space-sepd
    (lit/raw :OTHERS)
    (lit/raw :=>) ;lol
    b)))


(defn- sigconfn
  "Given a constant identifier (string or symbol), it's type, and an optional
  default, represents the 'CONSTANT id : type [:= default]' syntax
  Retruns a function that, when given an id returns the string" 
  [sigcon typ & default]
  (fn [id] 
    (apply expr/space-sepd
      (lit/raw sigcon)
      (lit/raw id)
      (lit/raw \:)
      (lit/raw typ)
      (if-not (empty? default)
        (cons (lit/raw ":=") default) '()))))

(defn- sigcon
  "Given a constant identifier (string or symbol), it's type, and an optional
  default, represents the 'CONSTANT id : type [:= default]' syntax"
  [sigcon id typ & default]
  ((apply sigconfn sigcon typ default) id))

(def constant (partial sigcon :CONSTANT))
(def signal (partial sigcon :SIGNAL))
(def variable (partial sigcon :VARIABLE))

(def constant-fn (partial sigcon :CONSTANT))
(def signal-fn (partial sigcon :SIGNAL))
(def variable-fn (partial sigcon :VARIABLE))


(defn assign-signal!
  "Given a signal id and what it should be assigned to, represents the
  'id <= towhat' syntax"
  [id towhat]
  (expr/space-sepd
    (lit/raw id)
    (lit/raw "<=")
    towhat))

(defn assign-variable!
  "Given a signal id and what it should be assigned to, represents the
  'id <: towhat' syntax"
  [id towhat]
  (expr/space-sepd
    (lit/raw id)
    (lit/raw ":=")
    towhat))

(defn port
  "Given partitions of three arguments, each set having a signal identifier, the
  direction of the signal (:in, :out, or :inout), and the signal's type,
  represents the PORT(id : direction type; ...) syntax"
  [& args]
  (expr/concated (lit/raw :PORT) (expr/parend
    (apply expr/sepd (lit/raw ";\n")
      (map (fn [argset]
        (let [[id inout typ] argset]
          (expr/space-sepd
            (lit/raw id)
            (lit/raw \:)
            (lit/raw inout)
            (lit/raw typ))))
        args)))))

(defn- lib-loaduse
  "Given one or more libraries that need to be imported, represents the
  'LIBRARY/USE lib1, lib2' syntax"
  [libuse & libs]
  (expr/space-sepd
    (lit/raw libuse)
    (apply expr/comma-sepd (map lit/raw libs))))

(def lib-load (partial lib-loaduse :LIBRARY))
(def lib-use (partial lib-loaduse :USE))

(defn concat-elements
  "Given one or more arrays/elements, represents the 'a & b & c ...'
  concatanation syntax"
  [& args]
  (apply expr/sepd (lit/raw " & ") args))

(comment [
  (proto/to-str (paren-call :func (lit/string "ohai") (lit/num2 "001")))
  (proto/to-str (downto (lit/num10 8) (lit/num2 "01")))
  (proto/to-str (array-of :BIT
    (downto (lit/num10 7) (lit/num10 0))
    (downto (lit/num10 7) (lit/num10 0))))
  (proto/to-str (array (lit/bit 0) (lit/bit 1)))
  (proto/to-str (lit/bit 0))

  (proto/to-str (array
    (array (lit/bit 0) (lit/bit 1))
    (array (lit/bit 0) (lit/bit 1))))
  (proto/to-str (others (lit/bit 0)))
  (proto/to-str (constant :a :REAL))
  (proto/to-str (constant :a :REAL (lit/num10 25)))
  (proto/to-str (signal :a :REAL))
  (proto/to-str (signal :a :REAL))

  (proto/to-str (signal :a :REAL (lit/num10 25)))
  (proto/to-str (assign-signal! :a (lit/num2 "1001")))
  (proto/to-str (lit/raw "jafe"))
  (proto/to-str (port [:sig1 :in :real]
                      [:sig2 :out :real]))
  (proto/to-str (lib-load "IEEE" "HARDI"))
  (proto/to-str (lib-use "IEEE.STD_LOGIC_1164" "HARDI.Devices.All"))
  (proto/to-str (concat-elements (lit/bit 0) (lit/bit 1)))
])
