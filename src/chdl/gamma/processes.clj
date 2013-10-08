(ns chdl.gamma.processes
  (:require [chdl.alpha.literal :as lit]
            [chdl.beta.math :as m]
            [chdl.beta.comp :as c]
            [chdl.beta.process :as proc]
            [chdl.alpha.expr :as expr]
            [chdl.alpha.proto :as proto]))
(comment 

;; Ideally, I'd like to do something like this:

  (let-proc [some-variable (variable (bit 0))
             some-sig (signal (bit 0)) 
             :watch clk rst]
    (cond 
      (high? rst)                                 (assign-signal! state (bit-vec "0000"))
      (low? load-n)                               (assign-signal! state (unsigned input))
      (and (high? clk) (event? clk) (low? up-n))  (assign-signal! state (inc state))
      (and (high? clk) (event? clk) (high? up-n)) (assign-signal! state (dec state))))


    process(clk, rst)
      signal some_sig : '0';
      variable some_variable : '0';
    begin
            if(rst = '1') then
                state <= "0000";
            elsif(load_n = '0') then
                state <= unsigned(input);
            elsif (clk='1' and clk'event) then
                if (up_n = '0') then
                    state <= state + 1;
                elsif(up_n = '1') then
                    state <= state - 1;
                end if;
            end if;
    end process;



)
