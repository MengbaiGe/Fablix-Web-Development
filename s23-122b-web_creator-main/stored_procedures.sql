use moviedb

delimiter $$
create procedure add_stars(in n text, in b int)
begin
	declare newid varchar(10);
	select concat('nm', lpad(cast(substring(max(id),3) as decimal)+1,7,'0')) into newid from stars;
	insert into stars(id, name, birthYear) values(newid,n,b);
end
$$

delimiter ;

delimiter $$
create procedure add_movies(in tit text, in ye int, in dir text, in starname text, in staryear int, in gen text)
begin
	declare newid varchar(10);
	declare mc int;
	declare c int;
	declare sid varchar(10);
	declare gid int;

	select count(*) into mc from movies where title=tit and year=ye and director=dir;
	if (mc=0) then
		select concat('tt', lpad(cast(substring(max(id),3) as decimal)+1,7,'0')) into newid from movies;
		select count(*) into c from stars where name = starname and birthYear = staryear;

		insert into movies(id, title, year, director) values(newid, tit,ye,dir);
		if (c=0) then
			call add_stars(starname, staryear);
		end if;
		select max(id) into sid from stars where name = starname;
		insert into stars_in_movies(starId,movieId) values (sid,newid);
		select count(*) into c from genres where name = gen;
		if (c=0) then
			insert into genres(name) values (gen);
		end if;
		select id into gid from genres where name = gen;
		insert into genres_in_movies(genreId,movieId) values(gid,newid);
		select concat('successfully inserted Movie Id: ',newid,', Star Id: ',sid,', Genre ID: ',gid) as msg;
	else
		select 'error: movie already exists' as msg;
	end if;


end
$$

delimiter ;

