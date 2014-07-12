package com.korzh.poehali.common.network;

import android.os.Handler;

import com.korzh.poehali.common.util.C;
import com.korzh.poehali.common.util.U;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.util.ArrayList;

/**
 * Created by vladimir on 7/1/2014.
 */
