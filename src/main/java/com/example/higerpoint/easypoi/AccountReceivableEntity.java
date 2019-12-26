package com.example.higerpoint.easypoi;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author chezhenqi
 * @date 2019/8/5 星期一
 * @time 15:09
 * @description baoli
 */
@Data
public class AccountReceivableEntity {
    /**
     * 买受人
     */
    @Excel(name = "买受人")
    private String applyPerson;

    /**
     * 社会统一信用代码
     */
    @Excel(name = "社会统一信用代码")
    private String companyNumber;
    /**
     * 商务合同编号
     */
    @Excel(name = "商务合同编号")
    private String businessContractNo;

    /**
     * 商务合同名称
     */
    @Excel(name = "商务合同名称")
    private String businessContractName;

    /**
     * 发票号码
     */
    @Excel(name = "发票号码")
    private String billNo;

    /**
     * 发票日期
     */
    @Excel(name = "发票日期", format = "yyyy-MM-dd HH:mm:ss")
    private Date billDate;

    /**
     * 发票金额
     */
    @Excel(name = "发票金额")
    private BigDecimal billAmount;

    /**
     * 应收账款金额
     */
    @Excel(name = "应收账款金额")
    private BigDecimal receivableAmount;

    /**
     * 到期日
     */
    @Excel(name = "到期日", format = "yyyy-MM-dd HH:mm:ss")
    private Date untilDate;

    /**
     * 可申请融资金额
     */
    @Excel(name = "可申请融资金额")
    private BigDecimal allowApplyAmount;

    /**
     * 导入是否成功：N:否;Y:是;
     */
    @Excel(name = "是否成功导入")
    private String isSuccess;

    /**
     * 导入id
     */
    @Excel(name = "导入成功ID")
    private String importId;

    /**
     * 导入失败原因
     */
    @Excel(name = "导入失败原因")
    private String refuseReason;

}
