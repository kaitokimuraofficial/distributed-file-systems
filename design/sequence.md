# ファイルの受け取り
```mermaid
sequenceDiagram

  participant FC as FileCache
  actor C as Client
  participant ES as EntryServer　
  participant FS as FileServer

  C->>+ES: Connect()
  ES-->>-C: return clientId 
  C->>+ES: getFile(clientId, filePath)
  ES->>FS: getFile(clientId, filePath)
  FS-->>ES: return File
  ES-->>-FC: return File
```
- 全てのClientはEntryServer(ES)にのみアクセスする
- ESはClientにclientIdを付与する
- ESとFSがやりとりしてファイルに関する操作を行う
- clientIdとファイルに関する命令を受けてESがFSに対してなんらかの操作をする。もしファイルの受け渡しがあればそれをClientのFileCache(FC)に渡す

# FileCacheからファイルをRead
```mermaid
sequenceDiagram

  actor C as Client
  participant CE as CacheHandler
  participant FC as FileCache

  C->>+CE: Read(filePath)
  CE->>+FC: getMethod(file)
  FC-->>-CE: return File
  CE->>-C:  return File
```

# FileCacheからファイルをwrite
```mermaid
sequenceDiagram

  actor C as Client
  participant CE as CacheHandler
  participant FC as FileCache

  C->>+CE: Write(file)
  CE->>+FC: setMethod(file)
  FC-->>-CE: return bool
  CE->>-C:  return bool
```

- ClientはCacheHandlerを通じてFileCacheからFileに関する情報を得る
**CacheHandlerを挟む理由**
  - 他のClientのwriteによってFileCache内の更新が起こるときに、その更新をFileCacheではなく、CacheHandlerにしてほしいから。
  - FileCacheはあくまでもFileの保管庫であり、その保管庫を操作するのは保管庫自身ではない方がいいと思う。
  - Clientに担当させないのは、ユーザーにFileCacheの管理を意識させたくないから
