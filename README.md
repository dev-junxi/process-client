# process-client
**ProcessClient** 是一个轻量级 Java 组件，旨在通过链式编程方式提高业务代码流程化开发的可维护性，定义统一开发范式便于团队协同工作，并内置节点执行快照功能，帮助开发者清晰定义主流程、便捷高效追踪线上问题。

### 开发中...

 ### ✨ 特性

- 🚀 链式调用，主流程一目了然
- 🧠 每步骤快照记录，轻松定位线上问题
- 🔍 面向可追踪、可观测的业务系统设计、支持分布式链路追踪
- 🛠️ 易于集成，零侵入
- 🔧 可修改快照存储级别 [info,debug,off]


### 🌟 快速开始

- 步骤一：注入ProcessClient
```java
@Autowired
private ProcessClient processClient;
```
- 步骤二：通过编排Processor执行业务代码
  - FundAccountProcessParam 需要实现 ProcessParam 接口 ：用于传递业务参数
  - StoreAccountProcessor 需要实现 BaseProcessor 接口 ： 处理业务逻辑，建议一个Processor中仅执行一个流程节点
  - FundAccountResult 需要实现 ProcessResult 接口 ： 用于保存每个节点的执行结果
```java
public FundAccountResult getFundAccount(FundAccountProcessParam param) {
        return processClient.param(param)
                .processors(spec -> spec.param("hostMethod", this.getClass().getName() + ".getFundAccount"))
                .processors(new StoreAccountProcessor(storeService))// 1.获取门店账户信息
                .processors(new PlatformCompanyAccountProcessor()) // 2.获取平台公司账户信息
                .processors(spec -> spec.param("platformCompanyConfig", platformCompanyConfig)) 
                .processors(new CooperationCompanyAccountProcessor(cooperationCompanyService)) // 3.获取合作公司账户信息
                .processors(MysqlProcessor.Builder().build())
                .processResult(new FundAccountResult())
                .flow()
                .execute()
                .entity(FundAccountResult.class);
    }

```
- 当然，Processor也可以通过实现LProcessor函数式接口的方式进行编排，快速开发
- 另外，合理利用ProcessClient内置的Context以实现所需的变量传递

  
### 📔process_snapshot
- 我们能通过process_snapshot追踪ProcessClient执行结果
- 通过 chain_id 可进行分布式追踪，目前仅支持 Feign 调用自动传递 chain_id ；通过向 Context 中 setter chainId 和 chainIndex 可进行手动传递
- 每一次HTTP请求的响应头中我们可以通过 X-PROCESS-CHAIN-ID 追踪 chain_id，进行BUG快速排查
- 扩展
  - 通过实现 LogProcessor 接口 您能定制快照存储方法，目前组件仅支持 MYSQL
<img width="1168" height="325" alt="image" src="https://github.com/user-attachments/assets/6cde9938-dd33-4381-8d10-cae150fbf21f" />



### ☑️可选
- ProcessClient 在执行过程中如遇异常，将通过 LoggerFactory 记录 error 级别日志，日志中包含 chain_id，便于追踪。可配合日志系统收集并对接消息平台进行异常告警，通知开发人员快速排查处理。
