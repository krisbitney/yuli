function userQuery(username) {
  return `https://www.instagram.com/web/search/topsearch/?query=${username}`;
}

function followersQuery(userId, after) {
  return (
    `https://www.instagram.com/graphql/query/?query_hash=c76146de99bb02f6415203be841dd25a&variables=` +
    encodeURIComponent(
      JSON.stringify({
        id: userId,
        include_reel: true,
        fetch_mutual: true,
        first: 46,
        after: after,
      })
    )
  );
}

function followingsQuery(userId, after) {
  return (
    `https://www.instagram.com/graphql/query/?query_hash=d04b0a864b4b54837c0d870b0e77e076&variables=` +
    encodeURIComponent(
      JSON.stringify({
        id: userId,
        include_reel: true,
        fetch_mutual: true,
        first: 50,
        after: after,
      })
    )
  );
}

async function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

async function findFollowers(username) {
  const result = {
    followers: [],
    followings: [],
  };

  const userQueryResponse = await fetch(userQuery(username));
  const userQueryJson = await userQueryResponse.json();
  const userId = userQueryJson.users[0].user.pk;

  let after = null;
  let hasNext = true;

  while (hasNext) {
    await sleep(400);

    const query = followersQuery(userId, after);
    const response = await fetch(query)
    const json = await response.json();

    const current = json.data.user.edge_followed_by;

    const users = current.edges.map(({ node }) => ({
      username: node.username,
      full_name: node.full_name,
      profile_pic_url: node.profile_pic_url,
    }));
    result.followers.push(users);

    hasNext = current.page_info.has_next_page;
    after = current.page_info.end_cursor;
  }

  after = null;
  hasNext = true;

  while (hasNext) {
    await sleep(400);
    const query = followingsQuery(userId, after);
    const response = await fetch(query)
    const json = await response.json();

    const current = json.data.user.edge_follow;

    const users = current.edges.map(({ node }) => ({
      username: node.username,
      full_name: node.full_name,
      profile_pic_url: node.profile_pic_url,
    }));
    result.followings.push(users);

    hasNext = current.page_info.has_next_page;
    after = current.page_info.end_cursor;
  }

  return result;
}

async function readFollowersToJson(username) {
  try {
    const followers = await findFollowers(username);
    const result = JSON.stringify(followers, null, 2);
    console.log(result);
    return result;
  } catch (e) {
    return "Failed to find followers: " + e.toString();
  }
}

