from MemoSqliteFunctions import *
import socket
import threading


HOST = '0.0.0.0'
PORT = 22222
BUFF_SIZE = 4096

def service_run():
    # 建立Socket连接, AF_INEF说明使用IPv4地址, SOCK_STREAM指明TCP协议
    serverSocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serverSocket.bind((HOST, PORT))
    serverSocket.listen(3)# 监听 

    print(f'Run at {HOST}:{PORT}')

    while True:
        # 接收TCP连接, 并返回新的Socket对象
        sk, addr = serverSocket.accept()
        print(f"客户端: {addr} 链接")

        task = threading.Thread(target = TCP_task, args=(sk, ), name = 'tcp_task')
        task.start()

    else:
        conn.close()


def TCP_task(sk : socket.socket) -> None:
    haveDB = False
    conn = None
    query = None
    data_list, iter = None, 0

    while True:
        try:
            # 接收客户端发送的数据
            data = sk.recv(BUFF_SIZE)
            data = data.decode('utf-8')
            print('receive >>>', data)

            # 首次消息 创建数据库
            if not haveDB:
                DB_name, DB_key, query = data.split(SEP)
                DB_name += '.db'

                # 若收到download请求, 先判断表是否存在, 若不在给予no_table回复
                if query == 'download':
                    if not os.path.exists(DB_PATH + DB_name):
                        sk.send('no_table'.encode('utf-8'))
                        print(f'无表{DB_name}!')
                        raise Exception(f'无表{DB_name}!')
                conn = open_database(DB_name, DB_key)


                if conn == None:
                    sk.send('deny'.encode('utf-8'))
                    print('拒绝访问...')
                    raise Exception('数据库未打开, 可能密码错误...')

                # 若处理download请求, 提前提取好信息
                if query == 'download':
                    data_list, iter = getAllMemo(conn), 0
                haveDB = True
                sk.send('ok'.encode('utf-8'))

            # 后续消息
            else:
                if query == 'upload':
                    # 存入一条数据
                    addOneMemo(conn, data)
                    sk.send('ok'.encode('utf-8'))

                elif query == 'download':
                    if data == 'next':
                        if iter < len(data_list):
                            item = data_list[iter]
                            info = item[1] + SEP + item[2] + SEP + item[3]
                            print(info)
                            sk.send(info.encode('utf-8'))
                            iter += 1
                        else:
                            print('finish')
                            sk.send('finished'.encode('utf-8'))
                            sk.close()
                    else:
                        sk.send('deny'.encode('utf-8'))
                        sk.close()
                        
                    
            print('|-- done.')
            
        # except BrokenPipeError as e:
        #     print(f'{type(e)} {str(e)}\n可能客户端链接断开...')
        #     sk.close()
        #     print('break')
        #     break
        except Exception as e:
            print(f'{type(e)} {str(e)}\n可能客户端链接断开...')
            sk.close()
            print('break')
            break


if __name__ == '__main__':
    service_run()