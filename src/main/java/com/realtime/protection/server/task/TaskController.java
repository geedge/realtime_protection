package com.realtime.protection.server.task;

import com.realtime.protection.configuration.entity.task.Task;
import com.realtime.protection.configuration.entity.task.TaskCommandInfo;
import com.realtime.protection.configuration.entity.user.UserFull;
import com.realtime.protection.configuration.exception.DorisStartException;
import com.realtime.protection.configuration.response.ResponseResult;
import com.realtime.protection.configuration.utils.File;
import com.realtime.protection.configuration.utils.enums.StateEnum;
import com.realtime.protection.configuration.utils.enums.audit.AuditStatusEnum;
import com.realtime.protection.server.command.CommandService;
import com.realtime.protection.server.defense.object.ProtectObjectService;
import com.realtime.protection.server.defense.templatenew.TemplateService;
import com.realtime.protection.server.rule.dynamicrule.DynamicRuleService;
import com.realtime.protection.server.rule.staticrule.StaticRuleService;
import com.realtime.protection.server.task.status.StateChangeService;
import com.realtime.protection.server.whitelist.WhiteListService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.Base64;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController implements TaskControllerApi {

    private final TaskService taskService;
    private final StaticRuleService staticRuleService;
    private final DynamicRuleService dynamicRuleService;
    private final ProtectObjectService protectObjectService;
    private final WhiteListService whiteListService;
    private final TemplateService templateService;

    private final CommandService commandService;
    private final StateChangeService stateChangeService;

    public TaskController(TaskService taskService, StaticRuleService staticRuleService, DynamicRuleService dynamicRuleService, ProtectObjectService protectObjectService, WhiteListService whiteListService,  TemplateService templateService, CommandService commandService, StateChangeService stateChangeService) {
        this.taskService = taskService;
        this.staticRuleService = staticRuleService;
        this.dynamicRuleService = dynamicRuleService;
        this.protectObjectService = protectObjectService;
        this.whiteListService = whiteListService;
        this.templateService = templateService;
        this.commandService = commandService;
        this.stateChangeService = stateChangeService;
    }

    @Override
    @PostMapping("/new")
    public ResponseResult newTask(@RequestBody @Valid Task task,
                                  @Autowired HttpServletRequest request) {

        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        if (user != null) {
            task.setTaskCreateUsername(user.name);
            task.setTaskCreateUserId(user.uid);
            task.setTaskCreateDepart(user.getOrgName());
            task.setAuditUserDepartCode(user.getOrgCode());
        }
        //事件类型的用户权限校验、动作逻辑性校验
        taskService.eventTypeValid(task);

        //冲突性
//        taskService.chongtuValid(task);

        Long taskId = taskService.newTask(task);

        if (taskId > 0) {
            return ResponseResult.ok()
                    .setData("task_name", task.getTaskName())
                    .setData("task_id", taskId)
                    .setData("success", true);
        }

        return ResponseResult.error()
                .setData("task_name", task.getTaskName())
                .setData("task_id", 0)
                .setData("success", false);
    }
    private boolean verifyHmac(String data, String signature, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes());
        String expectedSignature = Base64.getEncoder().encodeToString(hmacBytes);
        System.out.println(expectedSignature);
        return expectedSignature.equals(signature);
    }
    // API推送Endpoint
    @Override
    @PostMapping("/api/new")
    public ResponseResult newTaskWithAPI(@RequestBody @Valid TaskCommandInfo taskCommandInfo,
                                         @Autowired HttpServletRequest request) throws NoSuchAlgorithmException, InvalidKeyException {


        String signature = request.getHeader("X-Signature");
        if (signature == null){
            throw new IllegalArgumentException("HMAC签名值为空，认证失败");
        }
        String data = String.valueOf(taskCommandInfo.hashCode());
        String apiKey = "gyusygwefweuu2135634";
        if (!verifyHmac(data, signature, apiKey)) {
            throw new IllegalArgumentException("HMAC签名校验失败，认证失败"+String.valueOf(taskCommandInfo.hashCode()));
        }
        Long taskId = taskService.newTaskUsingCommandInfo(taskCommandInfo);
//        Long taskId =45377L;
        if (taskId <= 0) {
            return ResponseResult.invalid()
                    .setData("taskId", -1)
                    .setData("success", false);
        }

        commandService.createCommand(taskCommandInfo);

        return ResponseResult.ok()
                .setData("taskId", taskId)
                .setData("success", true)
                .setData("command_hash",taskCommandInfo.hashCode());
    }

    @GetMapping("/queryAttachment")
    @Override
    public ResponseResult queryAttachment(@RequestParam("task_id") Long taskId)  {
        List<File> files = taskService.selectFilesByStaticRuleId(taskId);

        return ResponseResult.ok()
                .setData("files", files);
    }

    //上传附件
    @PostMapping("/uploadAttachment")
    @Override
    public ResponseResult uploadAttachment(@RequestParam("task_id") Long taskId,
                                           MultipartFile file) throws IOException {

//        String uploadPath = "d:\\";
        String uploadPath = "/task_file";
        //获取文件上传名称
        String fileName=file.getOriginalFilename();
        //获取文件保存全路径
        String savePath=uploadPath+"/"+fileName;
        //获取文件大小
        Long fileSize=file.getSize();
        //获取文件类型
        String fileType=file.getContentType();
        java.io.File newFile=new java.io.File(savePath);
        // 获取文件的父目录
        java.io.File parentDir = newFile.getParentFile();
        // 如果父目录不存在，则创建该目录
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        //TODO 注意要将文件保存到本地路径中
        file.transferTo(newFile);
        File saveFile=new File();
        saveFile.setFileName(fileName);
        saveFile.setFileSize(fileSize);
        saveFile.setFileType(fileType);
        saveFile.setFilePath(savePath);
        saveFile.setTaskId(taskId);
        taskService.saveFile(saveFile);

        return ResponseResult.ok();
    }



    //文件下载
    @GetMapping("downAttachment")
    @Override
    public ResponseEntity<Resource> downFile(@RequestParam Integer id) throws MalformedURLException, UnsupportedEncodingException {
        //首先根据id，从数据库获取文件信息
        File downFile=taskService.selectFileById(id);
        if (downFile!=null){
            String path= downFile.getFilePath();
            //本地路径地址转为url编码路径
            URI urlPath= Paths.get(path).toUri();
            Resource resource=new UrlResource(urlPath);
            if (resource.exists()){
                HttpHeaders headers=new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM+"");
                headers.add(HttpHeaders.CONTENT_LENGTH,downFile.getFileSize()+"");
                //注意文件名处要改为URL编码
                headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" +
                        URLEncoder.encode(downFile.getFileName(), "utf-8") + "\"");
                return ResponseEntity.ok().headers(headers).body(resource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @Override
    @GetMapping("/query")
    public ResponseResult queryTasks(@RequestParam(value = "task_status", required = false) Integer taskStatus,
                                     @RequestParam(value = "task_type", required = false) Integer taskType,
                                     @RequestParam(value = "task_name", required = false) String taskName,
                                     @RequestParam(value = "task_creator", required = false) String taskCreator,
                                     @RequestParam(value = "audit_status", required = false) Integer auditStatus,
                                     @RequestParam(value = "task_act", required = false) String taskAct,
                                     @RequestParam(value = "task_auditor", required = false) String taskAuditor,
                                     @RequestParam(value = "task_source", required = false) String taskSource,
                                     @RequestParam(value = "rule_name", required = false) String ruleName,
                                     @RequestParam(value = "event_type", required = false) String eventType,
                                     @RequestParam(value = "create_time", required = false) LocalDate createTime,
                                     @RequestParam(value = "start_time", required = false) LocalDate startTime,
                                     @RequestParam(value = "protect_level", required = false) Integer protectLevel,

                                     @RequestParam("page") @Min(1) Integer page,
                                     @RequestParam("page_size") @Min(1) Integer pageSize) {


        String createDateStr = null , startTimeStr = null;
        if (createTime != null) {
            createDateStr = createTime.toString();
        }
        if(startTime != null) {
            startTimeStr = startTime.toString();
        }
        List<Task> tasks = taskService.queryTasks(taskStatus, taskType, taskName, taskCreator, auditStatus,
                taskAct, taskAuditor, taskSource, ruleName,
                eventType, createDateStr, startTimeStr,protectLevel, page, pageSize);
        return ResponseResult.ok()
                .setData("task_list", tasks)
                .setData("total_num", taskService.queryTaskTotalNum(taskStatus, taskType, taskName, taskCreator, auditStatus,
                        taskAct, taskAuditor, taskSource, ruleName,eventType, createDateStr, startTimeStr,protectLevel));
    }

    @Override
    @GetMapping("/{id}/query")
    public ResponseResult queryTask(@PathVariable @Min(1) Long id) {
        Task task = taskService.queryTask(id);

        if (task == null) {
            return ResponseResult.invalid().setMessage("无效Task ID，也许该ID对应的任务不存在？");
        }

        return ResponseResult.ok().setData("task", task);
    }

    @Override
    @PostMapping("/{taskId}/update")
    public ResponseResult updateTask(@PathVariable Long taskId, @RequestBody @Valid Task task) {
        task.setTaskId(taskId);
        return ResponseResult.ok()
                .setData("task_id", task.getTaskId())
                .setData("success", taskService.updateTask(task));
    }



    @Override
    @GetMapping("/{taskId}/audit/{auditStatus}")
    public ResponseResult changeTaskAuditStatus(@PathVariable @NotNull @Max(10) Integer auditStatus,
                                                @PathVariable @NotNull @Min(1) Long taskId,
                                                @Autowired HttpServletRequest request) {
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        String auditUserName = null;
        String auditUserId = null;
        String auditUserDepart = null;
        if (user != null) {
            auditUserName= user.name;
            auditUserId = user.uid;
            auditUserDepart = user.getOrgName();
        }
        return ResponseResult.ok()
                .setData("task_id", taskId)
                .setData("success", taskService.changeTaskAuditStatus(taskId, auditStatus,
                        auditUserName, auditUserId, auditUserDepart))
                .setData("audit_status", taskService.queryTaskAuditStatus(taskId));
    }

    @Override
    @DeleteMapping("/{taskId}/delete")
    public ResponseResult deleteTask(@PathVariable @NotNull @Min(1) Long taskId) {
        return ResponseResult.ok()
                .setData("task_id", taskId)
                .setData("success", taskService.deleteTask(taskId));
    }

    @Override
    @GetMapping("/{taskId}/running/{stateNum}")
    public ResponseResult changeTaskStatus(@PathVariable @NotNull @Min(0) @Max(6) Integer stateNum,
                                           @PathVariable @NotNull @Min(1) Long taskId) throws DorisStartException {
        return ResponseResult.ok()
                .setData("task_id", taskId)
                // 外部修改状态，需要进行状态检查
                .setData("success", stateChangeService.changeState(stateNum, taskId, false))
                .setData("status_now", taskService.queryTaskStatus(taskId));
    }

    @Override
    @GetMapping("/{taskId}/commands")
    public ResponseResult queryCommandInfos(@PathVariable Long taskId,
                                            @RequestParam(name = "src_ip", required = false) String sourceIP,
                                            @RequestParam(name = "src_port", required = false) String sourcePort,
                                            @RequestParam(name = "dst_ip", required = false) String destinationIP,
                                            @RequestParam(name = "dst_port", required = false) String destinationPort,
                                            @RequestParam(name = "page") @Min(1) Integer page,
                                            @RequestParam(name = "page_num") @Min(1) Integer pageNum) {
        List<TaskCommandInfo> taskCommandInfos = commandService.queryCommandInfos(
                taskId, sourceIP, sourcePort, destinationIP, destinationPort, page, pageNum);

        return ResponseResult.ok()
                .setData("success", true)
                .setData("commands", taskCommandInfos)
                .setData("total_num", commandService.queryCommandTotalNum(taskId, sourceIP, sourcePort, destinationIP, destinationPort));
    }

    @GetMapping("/{commandId}/valid/{isJudged}")
    public ResponseResult setCommandJudged(@PathVariable Integer isJudged,
                                           @PathVariable String commandId) {
        return ResponseResult.ok()
                .setData("success", commandService.setCommandJudged(commandId, isJudged))
//                .setData("success", true)
                .setData("command_id", commandId);
    }

    @GetMapping("/{commandId}/valid2/{isValid}")
    public ResponseResult setCommandValid(@PathVariable Integer isValid,
                                           @PathVariable String commandId) {
        if (isValid < 0 || isValid > 1) {
            return ResponseResult.invalid()
                    .setData("success", false)
                    .setData("command_id", commandId);
        }

        return ResponseResult.ok()
                .setData("success", commandService.setCommandValid(commandId, isValid))
//                .setData("success", true)
                .setData("command_id", commandId);
    }


    /**
     * 批量修改审核状态
     */
    @Override
    @PostMapping("/auditbatch")
    public ResponseResult updateTaskAuditStatusBatch(@RequestBody Map<Integer, Integer> idsWithAuditStatusMap,
                                                     @Autowired HttpServletRequest request) {
        List<Integer> errorIds = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry: idsWithAuditStatusMap.entrySet()) {
            Integer id = entry.getKey();
            Integer auditStatus = entry.getValue();
            if (id <= 0 || auditStatus < 0 || auditStatus > 2) {
                errorIds.add(id);
            }
        }
        if (!errorIds.isEmpty()) {
            return ResponseResult.invalid()
                    .setData("tasks_id", errorIds)
                    .setData("success", false);
        }
        //从http首部session字段获取用户信息
        HttpSession session = request.getSession();
        UserFull user = (UserFull) session.getAttribute("user");
        String auditUserName = null;
        String auditUserId = null;
        String auditUserDepart = null;
        if (user != null) {
            auditUserName= user.name;
            auditUserId = user.uid;
            auditUserDepart = user.getOrgName();
        }

        return ResponseResult.ok()
                .setData("success", taskService.updateAuditStatusBatch(idsWithAuditStatusMap,
                        auditUserName, auditUserId, auditUserDepart));
    }

    /**
     * 统计
     */
    @Override
    @GetMapping("/statistics")
    public ResponseResult statistics() {
        return ResponseResult.ok()
                .setData("total_num", taskService.queryTaskTotalNum(null, null, null, null, null,
                        null, null, null, null,null,null,null,null))
                .setData("running_num", taskService.queryTaskTotalNum(StateEnum.RUNNING.getStateNum(), null, null, null, null,
                        null, null, null, null,null,null,null,null))
                .setData("finished_num", taskService.queryTaskTotalNum(StateEnum.FINISHED.getStateNum(), null, null, null, null,
                        null, null, null, null,null,null,null,null))
                .setData("unaudit_num", taskService.queryAuditTaskTotalNum(AuditStatusEnum.PENDING.getNum()))
                .setData("audited_num", taskService.queryAuditTaskTotalNum(AuditStatusEnum.AUDITED.getNum()))
                .setData("rejected_num", taskService.queryAuditTaskTotalNum(AuditStatusEnum.RETURNED.getNum()))
                .setData("using_num", taskService.queryAuditTaskTotalNum(AuditStatusEnum.USING.getNum()));

    }

    @Override
    @PostMapping("/auditInfo/{ids}")
    public ResponseResult updateAuditInfo(@PathVariable List<Long> ids,
                                          @RequestBody Map<String, String> auditInfo) {
        if (auditInfo.get("auditInfo") == null || auditInfo.get("auditInfo").isEmpty()) {
            return ResponseResult.ok();
        }
        return ResponseResult.ok()
                .setData("success", taskService.updateAuditInfo(ids, auditInfo.get("auditInfo")));
    }

    @Override
    @GetMapping("/auditInfo/{id}")
    public ResponseResult queryAuditInfo(@PathVariable Integer id) {
        return ResponseResult.ok()
                .setData("auditInfo", taskService.queryAuditInfo(id));
    }

    @Override
    @GetMapping("/{id}/history")
    public ResponseResult queryHistory(@PathVariable Long id,
                                       @RequestParam(value = "page", required = true) Integer page,
                                       @RequestParam(value = "page_size", required = true) Integer pageSize) {

        return ResponseResult.ok()
                .setData("history", taskService.queryHistory(id, page, pageSize));
    }

    @Override
    @GetMapping("/unaudit/statistics")
    public ResponseResult queryUnauditStatistics() {
        return ResponseResult.ok()
                .setData("task", taskService.queryAuditTaskTotalNum(AuditStatusEnum.PENDING.getNum()))
                .setData("static_rule", staticRuleService.queryAuditStaticRuleTotalNum(AuditStatusEnum.PENDING.getNum()))
                .setData("dynamic_rule", dynamicRuleService.queryAuditDynamicRuleTotalNum(AuditStatusEnum.PENDING.getNum()))
                .setData("proobj_undit_num", protectObjectService.queryProtectObjectsTotalNum(null, null, null, null,
                        null, null, null, null, null,
                        AuditStatusEnum.getNumByState(AuditStatusEnum.PENDING.getState())))
                .setData("white_list", whiteListService.queryAuditWhiteListTotalNum(AuditStatusEnum.PENDING.getNum()))
                .setData("strategy_template", templateService.queryAuditTemplateTotalNum(AuditStatusEnum.PENDING.getNum()))
                ;
    }

    @Override
    @PostMapping("/send-pcap")
    public ResponseEntity<String> uploadPcap( MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("文件为空");
        }

        //            // Ensure the upload directory exists
//            File uploadDir = new File("C:\\");
//            if (!uploadDir.exists()) {
//                uploadDir.mkdirs();
//            }
//
//            // Save the file locally
//            Path path = Paths.get("C:\\" + file.getOriginalFilename());
//            Files.write(path, file.getBytes());

        // Here you can add logic to send the file to a server or process it as needed
        if(true) {throw new IllegalArgumentException("DNS报文校验失败");}
        return ResponseEntity.status(HttpStatus.OK).body("文件发送处置服务器成功: " + file.getOriginalFilename());
    }


    @Override
    @GetMapping("/result/push")
    public ResponseResult pushWhiteList() {
        List<TaskCommandInfo> taskCommandInfos = commandService.queryCommandInfos(
                null, null, null, null, null, 1, 20);

        return ResponseResult.ok()
                .setData("success", true)
                .setData("commands", taskCommandInfos);
    }

    @Override
    @GetMapping("/auditinfo/alert/{userId}")
    public ResponseResult auditInfoNotification(@PathVariable String userId){

        return ResponseResult.ok()
                .setData("success", true)
                .setData("alert", taskService.auditInfoNotification(userId));
    }

    @Override
    @PostMapping("/result/{systemName}")
    public ResponseResult receiveOtherSystemResult(@PathVariable String systemName,
                                                   @RequestBody Map<String, String> auditInfo){

        return ResponseResult.ok()
                .setData("success", true)
                .setData("task_id", auditInfo.get("task_id"));
    }

    @Override
    @GetMapping("/query/log/status")
    public ResponseResult queryTaskStausLog(@RequestParam("id") @Min(1) Long id ,
                                            @RequestParam("page") @Min(1) Integer page,
                                            @RequestParam("page_size") @Min(1) Integer pageSize) {
        List<Task> taskLog = taskService.queryTaskStausLog(id,page,pageSize);

        if (taskLog == null) {
            return ResponseResult.invalid().setMessage("无效Task ID，也许该ID对应的任务不存在？");
        }

        return ResponseResult.ok().setData("task_log", taskLog)
                .setData("total_num", taskService.queryTaskStausLogTotalNum(id));
    }



}