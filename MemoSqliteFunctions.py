import hashlib
import sqlite3
import os
from typing import List, Optional, Tuple

DB_PATH = './DATABASE_PATH/'
SEP = '_@#sE*p_'

def open_database(DB_name : str, DB_password : str) -> Optional[sqlite3.Connection]:
    '''
    第一轮权限判断
    1. 若没有数据库, 则建立数据库, 建立密码表, 储存用户发来的密码, 返回数据库
    2. 若有数据库, 判断密码是否正确, 若正确, 返回数据库
    '''

    ret = None
    if not os.path.exists(DB_PATH + DB_name):
        ret = create_table(DB_name, DB_password)
    else:
        ret = table_privilege(DB_name, DB_password)

    return ret

def create_table(DB_name : str, DB_password : str) -> sqlite3.Connection:
    '''创建一个名为DB_name的数据库, 在里面建一个password表, 把密码放进去, 创建memo表存数据'''
    if not os.path.exists(DB_PATH):
        os.makedirs(DB_PATH)

    conn = sqlite3.connect(DB_PATH + DB_name)
    cursor = conn.cursor()

    cursor.execute('create table password (id int primary key not null, value text);')
    cursor.execute('''create table memo (
        id INTEGER PRIMARY KEY AUTOINCREMENT
        , datas text
        , lastModifyTime text
        , title text
        );''')
    conn.commit()

    password_md5 = hashlib.md5(DB_password.encode('utf8')).hexdigest()
    cursor.execute('insert into password values (1, \'%s\')' % (password_md5))
    conn.commit()

    return conn

def table_privilege(DB_name : str, DB_password : str) -> Optional[sqlite3.Connection]:
    conn = sqlite3.connect(DB_PATH + DB_name)
    cursor = conn.cursor()

    password_md5 = hashlib.md5(DB_password.encode('utf8')).hexdigest()

    select_re = cursor.execute("SELECT value from password")
    password_in_DB = select_re.fetchone()[0]

    if password_in_DB == password_md5:
        return conn
    else:
        conn.close()
        return None

def addOneMemo(conn : sqlite3.Connection, data : str) -> bool:
    '''增加一条数据'''
# try:
    row_useless, title, lastModifyTime, datas = data.split(SEP)
    cursor = conn.cursor()

    cmd = 'select * from memo where title = \'%s\'' % (title)
    cursor.execute(cmd)

    if cursor.fetchone() == None: # 若没有此标题, 写入内容
        cmd = 'insert into memo(datas, lastModifyTime, title) values(\'%s\', \'%s\', \'%s\')' \
                % (datas, lastModifyTime, title)
        cursor.execute(cmd)
        conn.commit()
        print('add')
    else: # 若存在此标题, 修改内容
        cmd = 'update memo set lastModifyTime = \'%s\', datas = \'%s\' where title = \'%s\'' \
                % (lastModifyTime, datas, title)
        cursor.execute(cmd)
        conn.commit()
        print('mod')
    return True
# except Exception as e:
#     print(f'{type(e)}|{str(e)}')
#     return False
# finally:
#     return True


def getAllMemo(conn : sqlite3.Connection) -> List[Tuple[int, str, str, str]]:
    '''获取数据库所有数据'''
    cursor = conn.cursor()
    cursor.execute('select * from memo;')
    return cursor.fetchall()


