package com.github.jzhongming.wf.mvc.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import com.github.jzhongming.wf.mvc.log.Logger;
import com.github.jzhongming.wf.mvc.log.LoggerFactory;

public class UDPClient {

	private DatagramSocket sock;

	private InetSocketAddress addr;

	private Logger timeoutLog = LoggerFactory.getLogger(this.getClass());

	public UDPClient(String urlPath) {
		try {
			if (null == urlPath || urlPath.isEmpty()) {
				return;
			}

			String[] param = urlPath.split(":");
			if (param.length != 2) {
				throw new IllegalArgumentException(urlPath + " is format error; eg. ip:prot");
			}

			String ip = param[0];
			int port = Integer.valueOf(param[1].trim());

			addr = new InetSocketAddress(ip, port);
			sock = new DatagramSocket();

		} catch (Exception e) {
			timeoutLog.error("UDPClient init error.", e);
		}
	}

	public void close() {
		if (null != sock) {
			sock.close();
		}
	}

	public void send(String msg, String encode) {
		try {
			byte[] buf = msg.getBytes(encode);
			send(buf);
		} catch (Exception e) {
			timeoutLog.error("UDPClient send error.", e);
		}

	}

	public void send(String msg) {
		try {
			byte[] buf = msg.getBytes("utf-8");
			send(buf);
		} catch (Exception e) {
			timeoutLog.error("UDPClient send error.", e);
		}

	}

	public void send(byte[] buf) {
		if (null == addr || null == sock) {
			throw new IllegalArgumentException("UDPClient do not init!");
		}
		
		try {
			DatagramPacket dp = new DatagramPacket(buf, buf.length, addr);
			sock.send(dp);
		} catch (Exception e) {
			timeoutLog.error("UDPClient send error.", e);
		}

	}
}