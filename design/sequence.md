```mermaid
sequenceDiagram

  participant FC as FileCache
  actor C as Client
  participant ES as EntryServer　
  participant FS as FileServer

  C->>+ES: Connect()
  ES-->>-C: return clientId 
  C->>+ES: doSomething(clientId, filePath)
  ES->>FS: doSomething(clientId, filePath)
  FS-->>ES: return File
  ES-->>-FC: return File
  C->>+FC: Read(file)
  FC-->>-C: return File
```
- 全てのClientはEntryServer(ES)にのみアクセスする
- ESはClientにclientIdを付与する
- ESとFSがやりとりしてファイルに関する操作を行う
- clientIdとファイルに関する命令を受けてESがFSに対してなんらかの操作をする。もしファイルの受け渡しがあればそれをClientのFileCache(FC)に渡す

```mermaid
sequenceDiagram

  actor C as Client
  participant CE as CacheExecuter
  participant FC as FileCache

  C->>+CE: Order()
  CE->>+FC: getMethod, setMethod
  FC-->>-CE: return field of File
  CE->>-C:  return field of File
```
- ClientはCacheExecuterを通じてFileCacheからFileに関する情報を得る
**CacheExecuterに仲立ちさせる理由**
  - 他のClientのwriteによってFileCache内の更新が起こるときに、その更新をFileCacheではなく、CacheExecuterにしてほしいから。
  - FileCacheはあくまでもFileの保管庫であり、その保管庫を操作するのは保管庫自身ではない方がいいと思う。
  - Clientに担当させないのは、ユーザーにFileCacheの管理を意識させたくないから
