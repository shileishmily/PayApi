package com.youguu.pay.web.action;

import com.youguu.pay.client.api.PayClientProvider;
import com.youguu.pay.common.api.PayService;
import com.youguu.pay.common.bean.PayOrder;
import com.youguu.pay.common.response.Response;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Created by leo on 2018/1/19.
 */
@Path(value = "/pay")
public class PayAction {

	PayService payService = PayClientProvider.getPayService();

	/**
	 * 统计下单接口
	 * @param payCode 支付方式
	 * @param subject 订单标题（商品名称）
	 * @param body 订单描述
	 * @param price 订单总金额，单位为元，精确到小数点后两位
	 * @param outTradeNo 商户订单号
	 * @param bankType 银行卡类型
	 * @param deviceInfo 设备信息
	 * @param authCode 付款条码串
	 * @param wapUrl WAP支付链接
	 * @param wapName WAP支付网页名称
	 * @param openid 微信会员唯一标识
	 * @return
	 */
	@POST
	@Path(value = "/order")
	@Produces(value = {"text/xml", "application/json"})
	public Response makeOrder(@FormParam("payCode") int payCode,
							@FormParam("subject") String subject,
							@FormParam("body") String body,
							@FormParam("price") BigDecimal price,
							@FormParam("outTradeNo") String outTradeNo,
							@FormParam("bankType") String bankType,
							@FormParam("deviceInfo") String deviceInfo,
							@FormParam("authCode") String authCode,
							@FormParam("wapUrl") String wapUrl,
							@FormParam("wapName") String wapName,
							@FormParam("openid") String openid) {
		PayOrder payOrder = new PayOrder();
		String ip = "";

		payOrder.setSubject(subject);
		payOrder.setBody(body);
		payOrder.setPrice(price);
		payOrder.setOutTradeNo(outTradeNo);
		payOrder.setBankType(bankType);
		payOrder.setDeviceInfo(deviceInfo);
		payOrder.setAuthCode(authCode);
		payOrder.setWapUrl(wapUrl);
		payOrder.setWapName(wapName);
		payOrder.setOpenid(openid);

		Response response = payService.makeOrder(payCode, payOrder);

		return response;
	}


	/**
	 * 支付异步通知接口
	 * @param request 从request中第三方支付的订单通知信息
	 * @param payCode 支付方式
	 * @return
	 */
	@GET
	@Path(value = "/notify/{payCode}")
	@Produces("text/html;charset=UTF-8")
	public String notifyOrder(@Context HttpServletRequest request, @PathParam("payCode") int payCode) {
		Map parameterMap = request.getParameterMap();
		try {
			Response<Map<String, Object>> notifyResponse = payService.notify(payCode, parameterMap, request.getInputStream());

			if("0000".equals(notifyResponse.getCode())){
				Map<String, Object> resultMap = notifyResponse.getT();
				boolean success = payService.notifyVerify(payCode, resultMap);
				if(success){
					return "我的业务处理成功了，不要再重复通知了。";
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return "出错了，再通知一次";
	}

	/**
	 * 订单查询接口
	 * @param payCode 支付方式
	 * @param tradeNo 第三方交易号
	 * @param outTradeNo 商户订单号
	 * @return
	 */
	@POST
	@Path(value = "/queryOrder")
	@Produces("text/html;charset=UTF-8")
	public Response queryOrder(@FormParam("payCode") int payCode,
							 @FormParam("tradeNo") String tradeNo,
							 @FormParam("outTradeNo") String outTradeNo) {

		Response response = payService.queryOrder(payCode, tradeNo, outTradeNo);
		return response;
	}

	/**
	 * 订单关闭接口
	 * @param payCode 支付方式
	 * @param tradeNo 第三方交易号
	 * @param outTradeNo 商户订单号
	 * @return
	 */
	@POST
	@Path(value = "/closeOrder")
	@Produces("text/html;charset=UTF-8")
	public Response closeOrder(@FormParam("payCode") int payCode,
							 @FormParam("tradeNo") String tradeNo,
							 @FormParam("outTradeNo") String outTradeNo) {

		Response response = payService.closeOrder(payCode, tradeNo, outTradeNo);
		return response;
	}

	/**
	 * 退款接口
	 * @param payCode 支付方式
	 * @param tradeNo 第三方交易号
	 * @param outTradeNo 商户订单号
	 * @param refundAmount 退款总金额，单元：元，保留两位小数
	 * @param totalAmount 支付总金额，单元：元，保留两位小数
	 * @return
	 */
	@POST
	@Path(value = "/refund")
	@Produces("text/html;charset=UTF-8")
	public Response refund(@FormParam("payCode") int payCode,
						 @FormParam("tradeNo") String tradeNo,
						 @FormParam("outTradeNo") String outTradeNo,
						 @FormParam("refundAmount") BigDecimal refundAmount,
						 @FormParam("totalAmount") BigDecimal totalAmount) {

		Response response = payService.refund(payCode, tradeNo, outTradeNo, refundAmount, totalAmount);
		return response;
	}

	/**
	 * 退款查询接口
	 * @param payCode 支付方式
	 * @param tradeNo 第三方交易号
	 * @param outTradeNo 商户订单号
	 * @return
	 */
	@POST
	@Path(value = "/refundQuery")
	@Produces("text/html;charset=UTF-8")
	public Response refundQuery(@FormParam("payCode") int payCode,
							  @FormParam("tradeNo") String tradeNo,
							  @FormParam("outTradeNo") String outTradeNo) {

		Response response = payService.refundQuery(payCode, tradeNo, outTradeNo);
		return response;
	}
}
