事前準備:

  特に無し。

コンパイル:

  $ mvn compile

試験実行例:

  $ mvn exec:java -Dexec.args='bench set [OPTIONS]'
  $ mvn exec:java -Dexec.args='load -t mix [OPTIONS] [MODE-OPTIONS]'

実行可能jarの作成:

  $ mvn assembly:assembly

  出力ファイル: target/memcached_benchmark-{VERSION}-runnable.jar

実行可能jarの実行:

  $ java -jar memcached_benchmark-{VERSION}-runnable.jar {MODE} [OPTIONS]

引数の書式(ベンチマーク時): bench {MODE} [OPTIONS]

  MODE:
    set         値を保存する
    get         値を取得する
    get30       値を取得する(キーのヒット率30%)
    delete      値を削除する

  OPTIONS:
    -a {ADPT}   memcached実装を選択する (default: memcached)
        xmemcached      XMemcached
        memcached       Memcached client for Java
        citrusleaf      Citrusleaf
    -w          計測前にウォームアップする (default:しない)
    -s {ADDR}   memcachedサーバのアドレスを指定する (default: localhost:11211)
    -p {NUM}    スレッド数を指定する (default:10)
    -i {NUM}    スレッド毎の繰り返し数を指定する (default:10000)
    -r {NUM}    値の内容のリビジョンを指定する (default:0)

引数の書式(負荷試験時): load -t {MODE} [OPTIONS] [MODE-OPTIONS]

  MODE:
    get         値を取得する負荷試験
    mix		設定と取得を組み合わせた負荷試験

  OPTIONS:
    -a {ADPT}   memcached実装を選択する (default: memcached)
        memcached       Memcached client for Java
        xmemcached      XMemcached
        citrusleaf      Citrusleaf
    -s {ADDR}   memcachedサーバのアドレスを指定する (default: localhost:11211)
    -tp {NUM}   タスクの並列度を指定する (default: 1)
    -ti {NUM}   タスク実行のインターバルをmsecで指定する
                0を指定するとインターバル無く連続実行 (default: 0)
    -ri {NUM}   動作レポートを表示するインターバルをmsecで指定する
                (default: 5000)
    -rf {FNAME} 動作レポートを出力するファイルを指定する
                (default: ファイルに出力しない)
    -fc {NUM}   終了までのタスク実行回数 (default: 終了しない)
    -ft {NUM}   終了時刻を指定する (default: 終了しない)
                現在は終了までの秒数の形式でのみを指定できる

  MODE-OPTIONS (get): なし

  MODE-OPTIONS (mix):
    -mix-gk	取得するキーの数 (default: 10000)
    -mix-sk	設定するキーの数 (default: 3000)
    -mix-si	設定を実行するインターバルをsecで指定する (default: 3600)


補足：
Citrusleafでの使用方法

java -jar memcached_benchmark-0.7-runnable.jar bench set -a citrusleaf -s
[node's IP]:3000
