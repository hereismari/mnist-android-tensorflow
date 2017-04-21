# needed libraries
import tensorflow as tf

from tensorflow.examples.tutorials.mnist import input_data

logs_path = '/tmp/tensorflow_logs/convnet'

# mnist.train = 55,000 input data
# mnist.test = 10,000 input data
# mnist.validate = 5,000 input data
mnist = input_data.read_data_sets("MNIST_data/", one_hot=True)

# Implementing Convnet with TF
def weight_variable(shape, name=None):
    # break simmetry
    if name:
        w = tf.truncated_normal(shape, stddev=0.1, name=name)
    else:
        w = tf.truncated_normal(shape, stddev=0.1)

    return tf.Variable(w)


def bias_variable(shape, name=None):
    # avoid dead neurons
    if name:
        b = tf.constant(0.1, shape=shape, name=name)
    else:
        b = tf.constant(0.1, shape=shape)
    return tf.Variable(b)


# pool
def max_pool_2x2(x):
    return tf.nn.max_pool(x, ksize=[1, 2, 2, 1],
                          strides=[1, 2, 2, 1], padding='SAME')

def new_conv_layer(x, w):
	return tf.nn.conv2d(x, w, strides=[1, 1, 1, 1], padding='SAME')

# our network!!!

g = tf.Graph()

with g.as_default():

	# input data
	x = tf.placeholder(tf.float32, shape=[None, 28*28], name='input_data')
	x_image = tf.reshape(x, [-1, 28, 28, 1])
	# correct labels
	y_ = tf.placeholder(tf.float32, shape=[None, 10], name='correct_labels')

	# fist conv layer
	with tf.name_scope('convLayer1'):
		w1 = weight_variable([5, 5, 1, 32])
		b1 = bias_variable([32])
		convlayer1 = tf.nn.relu(new_conv_layer(x_image, w1) + b1)
		max_pool1 = max_pool_2x2(convlayer1)

	# second conv layer
	with tf.name_scope('convLayer2'):
		w2 = weight_variable([5, 5, 32, 64])
		b2 = bias_variable([64])
		convlayer2 = tf.nn.relu(new_conv_layer(max_pool1, w2) + b2)
		max_pool2 = max_pool_2x2(convlayer2)

	# flat layer
	with tf.name_scope('flattenLayer'):
		flat_layer = tf.reshape(max_pool2, [-1, 7 * 7 * 64])

	# fully connected layer
	with tf.name_scope('FullyConnectedLayer'):
		wfc1 = weight_variable([7 * 7 * 64, 1024])
		bfc1 = bias_variable([1024])
		fc1 = tf.nn.relu(tf.matmul(flat_layer, wfc1) + bfc1)

	# DROPOUT
	with tf.name_scope('Dropout'):
		keep_prob = tf.placeholder(tf.float32)
		drop_layer = tf.nn.dropout(fc1, keep_prob)

	# final layer
	with tf.name_scope('FinalLayer'):
		w_f = weight_variable([1024, 10])
		b_f = bias_variable([10])
		y_f = tf.matmul(drop_layer, w_f) + b_f
		y_f_softmax = tf.nn.softmax(y_f)

	# loss
	loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y_,
																  logits=y_f))

	# train step
	train_step = tf.train.AdamOptimizer(1e-4).minimize(loss)

	# accuracy
	correct_prediction = tf.equal(tf.argmax(y_f_softmax, 1), tf.argmax(y_, 1))
	accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

	# Create a summary to monitor loss tensor
	tf.summary.scalar("loss", loss)
	# Create a summary to monitor accuracy tensor
	tf.summary.scalar("accuracy", accuracy)
	# Merge all summaries into a single op
	merged_summary_op = tf.summary.merge_all()

	# init
	init = tf.global_variables_initializer()

	# Running the graph

	num_steps = 3000
	batch_size = 16
	test_size = 10000
	test_accuracy = 0.0

	sess = tf.Session()

	sess.run(init)
	# op to write logs to Tensorboard
	summary_writer = tf.summary.FileWriter(logs_path,
										   graph=tf.get_default_graph())

	for step in range(num_steps):
		batch = mnist.train.next_batch(batch_size)

		ts, error, acc, summary = sess.run([train_step, loss, accuracy,
											merged_summary_op],
										   feed_dict={x: batch[0],
													  y_: batch[1],
													  keep_prob: 0.5})
		if step % 100 == 0:
			train_accuracy = accuracy.eval({
				x: batch[0], y_: batch[1], keep_prob: 1.0}, sess)
			print('step %d, training accuracy %f' % (step, train_accuracy))
        '''
	print 'Done!'
	print 'Evaluating...'
	for i in xrange(test_size/50):
		batch = mnist.test.next_batch(50)
		acc = accuracy.eval({x: batch[0], y_: batch[1],
									   keep_prob: 1.0}, sess)
		if i % 10 == 0:
			print('%d: test accuracy %f' % (i, acc))
		test_accuracy += acc
	print 'avg test accuracy:', test_accuracy/(test_size/50.0)
        '''

# copying variables as constants to export graph
_w1 = w1.eval(sess)
_b1 = b1.eval(sess)
_w2 = w2.eval(sess)
_b2 = b2.eval(sess)
_wfc1 = wfc1.eval(sess)
_bfc1 = bfc1.eval(sess)
_w_f = w_f.eval(sess)
_b_f = b_f.eval(sess)

sess.close()

g2 = tf.Graph()
with g2.as_default():

	# input data
	x2 = tf.placeholder(tf.float32, shape=[None, 28*28], name='input')
	x2_image = tf.reshape(x2, [-1, 28, 28, 1])
	# correct labels
	y2_ = tf.placeholder(tf.float32, shape=[None, 10])

	w1_2 = tf.constant(_w1)
	b1_2 = tf.constant(_b1)
	convlayer1_2 = tf.nn.relu(new_conv_layer(x2_image, w1_2) + b1_2)
	max_pool1_2 = max_pool_2x2(convlayer1_2)

	w2_2 = tf.constant(_w2)
	b2_2 = tf.constant(_b2)
	convlayer2_2 = tf.nn.relu(new_conv_layer(max_pool1_2, w2_2) + b2_2)
	max_pool2_2 = max_pool_2x2(convlayer2_2)

	# flat layer
	flat_layer_2 = tf.reshape(max_pool2_2, [-1, 7 * 7 * 64])

	# fully connected layer
	wfc1_2 = tf.constant(_wfc1)
	bfc1_2 = tf.constant(_bfc1)
	fc1_2 = tf.nn.relu(tf.matmul(flat_layer_2, wfc1_2) + bfc1_2)

	# no dropout layer

	# final layer
	w_f_2 = tf.constant(_w_f)
	b_f_2 = tf.constant(_b_f)
	y_f_2 = tf.matmul(fc1_2, w_f_2) + b_f_2
	y_f_softmax_2 = tf.nn.softmax(y_f_2, name='output')

	# init
	init_2 = tf.global_variables_initializer()

	sess_2 = tf.Session()
        init_2 = tf.initialize_all_variables()
        sess_2.run(init_2)

        graph_def = g2.as_graph_def()
        tf.train.write_graph(graph_def, '', 'graph.pb', as_text=False)

