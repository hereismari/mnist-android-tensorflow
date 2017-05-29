# needed libraries
import tensorflow as tf
import os
import os.path as path
from tensorflow.python.tools import freeze_graph
from tensorflow.python.tools import optimize_for_inference_lib

from tensorflow.examples.tutorials.mnist import input_data

logs_path = 'logs/'
if not path.exists('out'):
    os.mkdir('out')
graph_save_path = 'out/mnist_convnet.graph.bin'
graph_ckp_path = 'out/mnist_convnet.ckpt'
frozen_graph_name = 'out/frozen_mnist_convnet.pb'
opt_graph_name = 'out/optimized_mnist_convnet.pb'

input_node_name = 'input'
keep_prob_node_name = 'keep_prob'
output_node_name = 'output'

mnist = input_data.read_data_sets("MNIST_data/", one_hot=True)

print("training start...")

# input data
x = tf.placeholder(tf.float32, shape=[None, 28*28], name=input_node_name)
x_image = tf.reshape(x, [-1, 28, 28, 1])
# 28*28*1
# correct labels
y_ = tf.placeholder(tf.float32, shape=[None, 10])

conv1 = tf.layers.conv2d(x_image, 64, 3, 1, 'same', activation=tf.nn.relu)
# 28*28*64
pool1 = tf.layers.max_pooling2d(conv1, 2, 2, 'same')
# 14*14*64

conv2 = tf.layers.conv2d(pool1, 128, 3, 1, 'same', activation=tf.nn.relu)
# 14*14*128
pool2 = tf.layers.max_pooling2d(conv2, 2, 2, 'same')
# 7*7*128

conv3 = tf.layers.conv2d(pool2, 256, 3, 1, 'same', activation=tf.nn.relu)
# 7*7*256
pool3 = tf.layers.max_pooling2d(conv3, 2, 2, 'same')
# 4*4*256

flatten = tf.reshape(pool3, [-1, 4*4*256])
fc = tf.layers.dense(flatten, 1024, activation=tf.nn.relu)
keep_prob = tf.placeholder(tf.float32, name=keep_prob_node_name)
dropout = tf.nn.dropout(fc, keep_prob)
logits = tf.layers.dense(dropout, 10)
outputs = tf.nn.softmax(logits, name=output_node_name)

# loss
loss = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=y_, logits=logits))

# train step
train_step = tf.train.AdamOptimizer(0.0001).minimize(loss)

# accuracy
correct_prediction = tf.equal(tf.argmax(outputs, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

tf.summary.scalar("loss", loss)
tf.summary.scalar("accuracy", accuracy)
merged_summary_op = tf.summary.merge_all()

saver = tf.train.Saver()
init_op = tf.global_variables_initializer()

with tf.Session() as sess:
    sess.run(init_op)

    tf.train.write_graph(sess.graph_def, '.', graph_save_path + '.txt')
    tf.train.write_graph(sess.graph_def, '.', graph_save_path, False)

    # Running the graph
    num_steps = 3000
    batch_size = 16

    # op to write logs to Tensorboard
    summary_writer = tf.summary.FileWriter(logs_path, graph=tf.get_default_graph())

    for step in range(num_steps):
    	batch = mnist.train.next_batch(batch_size)

    	ts, error, acc, summary = sess.run([train_step, loss, accuracy, merged_summary_op],
    									   feed_dict={x: batch[0],
    												  y_: batch[1],
    												  keep_prob: 0.5})
    	summary_writer.add_summary(summary, step)
    	if step % 100 == 0:
    		print('step %d, training accuracy %f' % (step, acc))

    saver.save(sess, graph_ckp_path)

print("training finished!")

freeze_graph.freeze_graph(graph_save_path, None, True, graph_ckp_path, \
            output_node_name, "save/restore_all", "save/Const:0", \
            frozen_graph_name, True, "")

input_graph_def = tf.GraphDef()
with tf.gfile.Open(frozen_graph_name, "rb") as f:
    input_graph_def.ParseFromString(f.read())

output_graph_def = optimize_for_inference_lib.optimize_for_inference(
        input_graph_def, [input_node_name, keep_prob_node_name], [output_node_name],
        tf.float32.as_datatype_enum)

f = tf.gfile.FastGFile(opt_graph_name, "wb")
f.write(output_graph_def.SerializeToString())

print("graph saved!")
