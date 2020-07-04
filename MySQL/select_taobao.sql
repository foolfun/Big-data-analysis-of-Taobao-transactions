select brand_id from user_log limit 10;
select month_,day_,cat_id from user_log limit 20;
select count(distinct user_id) from user_log where action='2' AND month_='11' AND day_='11';
select count(*) from user_log where action='2' and brand_id=7622  AND month_='7' AND day_='9';
select merchant_id,count(distinct user_id) from user_log where action='0'AND month_='11' AND day_='11' GROUP BY merchant_id;
select count(*) from user_log where gender=0 AND month_='11' AND day_='11' AND action='2';
select count(*) from user_log where gender=1 AND month_='11' AND day_='11' AND action='2';
select distinct user_id from user_log where action='2' GROUP BY user_id,month_,day_ HAVING COUNT(action='2')>5