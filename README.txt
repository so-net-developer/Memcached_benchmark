PREPARATION
===========
Nothing especially.

HOW TO COMPILE
==============

    $ mvn compile

EXECUTE TO EXAMINATE
====================

    $ mvn exec:java -Dexec.args='bench set [OPTIONS]'
    $ mvn exec:java -Dexec.args='load -t mix [OPTIONS] [MODE-OPTIONS]'

BUILD EXECUTABLE JAR
====================

    $ mvn assembly:assembly

Destination file: target/memcached_benchmark-{VERSION}-runnable.jar

EXECUTE WITH JAR
================

    $ java -jar memcached_benchmark-{VERSION}-runnable.jar {MODE} [OPTIONS]

COMMON OPTIONS
--------------

 * -kp {STR}   Prefix for keys.
 * -vv         Flag. Use valiable length values.
 * -vx {NUM}   Valiable max length for values. (default:100)

BENCHMARK TEST MODE
-------------------
Do benchmark test against KVS server, which have memcached protocol.

ARGUMENTS FORMAT: bench {MODE} [OPTIONS]

 * MODE
  + set         Benchmark test of SET operations.
  + get         Benchmark test of GET operations.
  + get30       Benchmark test of GET operations.  (Key hit rate is 30%)
  + delete      Benchmark test of DELETE operations.

 * OPTIONS
  + -a {ADPT}   Select KVS implementation adapter(default: memcached)
   - xmemcached     XMemcached
   - memcached      Memcached client for Java
  + -w          Make warm up before benchmark test. (default: NO)
  + -s {ADDR}   Address of KVS server. (default: localhost:11211)
  + -p {NUM}    Concurrent thread number. (default:10)
  + -i {NUM}    Iteration count for each thread. (default:10000)
  + -r {NUM}    Value contents revision. (default:0)

LOAD TEST MODE
--------------
Do high load test against KVS server, which have memcached protocol.

ARGUMENTS FORMAT: load -t {MODE} [OPTIONS] [MODE-OPTIONS]

 * MODE
  + get         Make load by GET operation.
  + mix		Make load by GET and SET operations.

 * OPTIONS
  + -a {ADPT}   Select KVS implementation adapter(default: memcached)
   - memcached      Memcached client for Java
   - xmemcached     XMemcached
  + -s {ADDR}   Address of KVS server. (default: localhost:11211)
  + -tp {NUM}   Concurrent task couunt. (default: 1)
  + -ti {NUM}   Interval of between two tasks im milliseconds.
                Zero cause invoke a next task as soon as possible. (default: 0)
  + -ri {NUM}   Interval to show log report in milliseconds.
                (default: 5000,  (= 5 seconds))
  + -rf {FNAME} Output log report to the file.
                (default: no file to output)
  + -fc {NUM}   Tasks count limitation to end load test
                (default: infinity, never end)
  + -ft {NUM}   Period to load test (default: never end by time).
                Currentry only support "seconds".
                (ex. '-ft 100' will end in 100 senconds)

 * MODE-OPTIONS (get): NONE

 *  MODE-OPTIONS (mix):
  + -mix-gk	Number of keys variation to GET operations. (default: 10000)
  + -mix-sk	Number of keys variation to SET operations. (default: 3000)
  + -mix-si	Interval to do SET operations in seconds. (default: 3600)

